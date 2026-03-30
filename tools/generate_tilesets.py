from pathlib import Path
import random

from PIL import Image, ImageDraw

TILE = 32
COLS = 4
ROWS = 4
OUT_DIR = Path(__file__).resolve().parent.parent / "assets" / "tilesets"


PREHISTORIC = {
    "grass": (90, 130, 64, 255),
    "grass_dark": (58, 92, 40, 255),
    "grass_light": (134, 170, 88, 255),
    "dirt": (122, 92, 58, 255),
    "dirt_dark": (94, 68, 41, 255),
    "sand": (181, 151, 94, 255),
    "stone": (108, 102, 88, 255),
    "stone_dark": (74, 68, 58, 255),
    "bone": (226, 212, 180, 255),
    "ember": (230, 102, 32, 255),
    "ember_glow": (250, 179, 78, 255),
    "water": (52, 143, 168, 255),
    "water_light": (108, 197, 199, 255),
}

FUTURE = {
    "metal": (82, 94, 112, 255),
    "metal_dark": (52, 60, 76, 255),
    "metal_light": (134, 149, 170, 255),
    "rust": (140, 86, 42, 255),
    "rust_dark": (92, 49, 21, 255),
    "hazard": (239, 194, 49, 255),
    "hazard_dark": (44, 45, 48, 255),
    "neon": (66, 237, 220, 255),
    "neon_soft": (145, 255, 247, 255),
    "oil": (26, 27, 31, 255),
    "warning": (218, 84, 73, 255),
}


def rect(draw, x0, y0, x1, y1, color):
    draw.rectangle((x0, y0, x1, y1), fill=color)


def speckle(draw, rng, colors, count, min_size=1, max_size=2):
    for _ in range(count):
        size = rng.randint(min_size, max_size)
        x = rng.randint(0, TILE - size)
        y = rng.randint(0, TILE - size)
        rect(draw, x, y, x + size - 1, y + size - 1, rng.choice(colors))


def border(draw, color):
    rect(draw, 0, 0, TILE - 1, 0, color)
    rect(draw, 0, TILE - 1, TILE - 1, TILE - 1, color)
    rect(draw, 0, 0, 0, TILE - 1, color)
    rect(draw, TILE - 1, 0, TILE - 1, TILE - 1, color)


def make_tile(fill):
    return Image.new("RGBA", (TILE, TILE), fill)


def prehistoric_grass(rng):
    tile = make_tile(PREHISTORIC["grass"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["grass_dark"], PREHISTORIC["grass_light"]], 50, 1, 2)
    for _ in range(7):
        x = rng.randint(2, 28)
        y = rng.randint(4, 28)
        rect(draw, x, y, x, y + 2, PREHISTORIC["grass_dark"])
        rect(draw, x + 1, y - 1, x + 1, y + 1, PREHISTORIC["grass_light"])
    return tile


def prehistoric_dirt(rng):
    tile = make_tile(PREHISTORIC["dirt"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["dirt_dark"], PREHISTORIC["sand"]], 64, 1, 2)
    for _ in range(6):
        x = rng.randint(2, 26)
        y = rng.randint(2, 26)
        rect(draw, x, y, x + 2, y + 1, PREHISTORIC["stone_dark"])
    return tile


def prehistoric_path(rng):
    tile = prehistoric_grass(rng)
    draw = ImageDraw.Draw(tile)
    path_x = rng.randint(10, 14)
    for y in range(TILE):
        width = 8 + ((y // 4) % 3)
        rect(draw, path_x - width, y, path_x + width, y, PREHISTORIC["dirt"])
        if y % 3 == 0:
            rect(draw, path_x - width + 2, y, path_x + width - 2, y, PREHISTORIC["sand"])
    return tile


def prehistoric_stone(rng):
    tile = make_tile(PREHISTORIC["stone"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["stone_dark"], PREHISTORIC["sand"]], 58, 1, 2)
    for offset in range(0, TILE, 8):
        rect(draw, offset, offset // 2, min(TILE - 1, offset + 6), min(TILE - 1, offset // 2 + 1), PREHISTORIC["stone_dark"])
    return tile


def prehistoric_mud(rng):
    tile = make_tile(PREHISTORIC["dirt_dark"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["dirt"], PREHISTORIC["stone_dark"]], 44, 1, 2)
    rect(draw, 7, 8, 24, 22, PREHISTORIC["stone_dark"])
    rect(draw, 9, 10, 22, 20, PREHISTORIC["dirt"])
    return tile


def prehistoric_bones(rng):
    tile = make_tile(PREHISTORIC["sand"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["dirt"], PREHISTORIC["bone"]], 36, 1, 2)
    rect(draw, 5, 18, 12, 20, PREHISTORIC["bone"])
    rect(draw, 9, 15, 11, 23, PREHISTORIC["bone"])
    rect(draw, 18, 8, 25, 10, PREHISTORIC["bone"])
    rect(draw, 21, 5, 23, 13, PREHISTORIC["bone"])
    return tile


def prehistoric_firepit(rng):
    tile = make_tile(PREHISTORIC["dirt_dark"])
    draw = ImageDraw.Draw(tile)
    for x, y in ((11, 11), (18, 11), (11, 18), (18, 18), (14, 9), (14, 20)):
        rect(draw, x, y, x + 3, y + 3, PREHISTORIC["stone"])
    rect(draw, 13, 13, 18, 18, PREHISTORIC["ember"])
    rect(draw, 14, 11, 17, 20, PREHISTORIC["ember_glow"])
    speckle(draw, rng, [PREHISTORIC["dirt"], PREHISTORIC["stone_dark"]], 28, 1, 1)
    return tile


def prehistoric_cliff(rng):
    tile = make_tile(PREHISTORIC["stone_dark"])
    draw = ImageDraw.Draw(tile)
    for y in range(0, TILE, 5):
        rect(draw, 0, y, TILE - 1, min(TILE - 1, y + 1), PREHISTORIC["stone"])
    speckle(draw, rng, [PREHISTORIC["stone"], PREHISTORIC["sand"]], 26, 1, 2)
    return tile


def prehistoric_bush(rng):
    tile = prehistoric_grass(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 7, 10, 24, 22, PREHISTORIC["grass_dark"])
    speckle(draw, rng, [PREHISTORIC["grass"], PREHISTORIC["grass_light"]], 24, 1, 2)
    return tile


def prehistoric_stump(rng):
    tile = prehistoric_grass(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 10, 10, 21, 22, PREHISTORIC["dirt"])
    rect(draw, 12, 12, 19, 20, PREHISTORIC["sand"])
    rect(draw, 15, 12, 16, 20, PREHISTORIC["dirt_dark"])
    rect(draw, 12, 16, 19, 17, PREHISTORIC["dirt_dark"])
    return tile


def prehistoric_fossil(rng):
    tile = make_tile(PREHISTORIC["sand"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["dirt"], PREHISTORIC["grass_light"]], 34, 1, 2)
    rect(draw, 8, 14, 23, 17, PREHISTORIC["bone"])
    for step in range(6):
        rect(draw, 16 - step, 16 - step, 17 + step, 17 - step, PREHISTORIC["bone"])
    return tile


def prehistoric_water(rng):
    tile = make_tile(PREHISTORIC["water"])
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [PREHISTORIC["water_light"], PREHISTORIC["stone_dark"]], 42, 1, 2)
    for x in range(0, TILE, 6):
        rect(draw, x, 6 + (x % 3), min(TILE - 1, x + 3), 7 + (x % 3), PREHISTORIC["water_light"])
    return tile


def prehistoric_totem(rng):
    tile = prehistoric_dirt(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 12, 6, 19, 25, PREHISTORIC["stone"])
    rect(draw, 14, 9, 17, 10, PREHISTORIC["bone"])
    rect(draw, 14, 15, 17, 16, PREHISTORIC["bone"])
    rect(draw, 13, 21, 18, 22, PREHISTORIC["bone"])
    return tile


def prehistoric_transition(rng):
    tile = make_tile(PREHISTORIC["grass"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 0, 0, TILE - 1, 14, PREHISTORIC["stone"])
    speckle(draw, rng, [PREHISTORIC["grass_dark"], PREHISTORIC["grass_light"]], 24, 1, 2)
    speckle(draw, rng, [PREHISTORIC["stone_dark"], PREHISTORIC["sand"]], 24, 1, 2)
    return tile


def future_metal(rng):
    tile = make_tile(FUTURE["metal"])
    draw = ImageDraw.Draw(tile)
    for x in range(0, TILE, 8):
        rect(draw, x, 0, x, TILE - 1, FUTURE["metal_dark"])
    for y in range(0, TILE, 8):
        rect(draw, 0, y, TILE - 1, y, FUTURE["metal_light"])
    speckle(draw, rng, [FUTURE["metal_dark"], FUTURE["metal_light"]], 30, 1, 1)
    return tile


def future_circuit(rng):
    tile = make_tile(FUTURE["metal_dark"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 4, 6, 27, 8, FUTURE["neon"])
    rect(draw, 11, 8, 13, 24, FUTURE["neon"])
    rect(draw, 13, 18, 24, 20, FUTURE["neon"])
    rect(draw, 22, 10, 24, 18, FUTURE["neon"])
    speckle(draw, rng, [FUTURE["metal"], FUTURE["metal_light"]], 26, 1, 2)
    return tile


def future_grid(rng):
    tile = make_tile(FUTURE["metal"])
    draw = ImageDraw.Draw(tile)
    for x in range(0, TILE, 8):
        rect(draw, x, 0, x + 1, TILE - 1, FUTURE["neon_soft"])
    for y in range(0, TILE, 8):
        rect(draw, 0, y, TILE - 1, y + 1, FUTURE["neon"])
    speckle(draw, rng, [FUTURE["metal_dark"]], 18, 1, 1)
    return tile


def future_hazard(rng):
    tile = make_tile(FUTURE["hazard"])
    draw = ImageDraw.Draw(tile)
    for stripe in range(-TILE, TILE * 2, 8):
        for step in range(8):
            x = stripe + step
            rect(draw, x, 0, x + 3, TILE - 1, FUTURE["hazard_dark"])
    rect(draw, 0, 0, TILE - 1, 2, FUTURE["warning"])
    rect(draw, 0, TILE - 3, TILE - 1, TILE - 1, FUTURE["warning"])
    return tile


def future_rust(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    speckle(draw, rng, [FUTURE["rust"], FUTURE["rust_dark"]], 40, 2, 3)
    return tile


def future_wreckage(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 7, 8, 24, 22, FUTURE["metal_dark"])
    rect(draw, 10, 10, 16, 15, FUTURE["warning"])
    rect(draw, 17, 15, 21, 20, FUTURE["rust"])
    rect(draw, 13, 18, 20, 20, FUTURE["neon"])
    return tile


def future_energy(rng):
    tile = make_tile(FUTURE["metal_dark"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 9, 9, 22, 22, FUTURE["metal"])
    rect(draw, 12, 12, 19, 19, FUTURE["neon"])
    rect(draw, 14, 6, 17, 25, FUTURE["neon_soft"])
    return tile


def future_conveyor(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    for x in range(4, TILE, 10):
        rect(draw, x, 13, x + 5, 18, FUTURE["hazard_dark"])
        rect(draw, x + 2, 11, x + 7, 16, FUTURE["hazard"])
    return tile


def future_block(rng):
    tile = make_tile(FUTURE["metal_dark"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 5, 5, 26, 26, FUTURE["metal"])
    rect(draw, 8, 8, 23, 23, FUTURE["metal_light"])
    rect(draw, 11, 11, 20, 20, FUTURE["metal"])
    border(draw, FUTURE["metal_dark"])
    return tile


def future_glass(rng):
    tile = make_tile(FUTURE["metal"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 5, 5, 26, 26, (92, 160, 168, 255))
    rect(draw, 8, 8, 23, 23, (134, 208, 216, 255))
    rect(draw, 10, 9, 21, 11, FUTURE["neon_soft"])
    return tile


def future_vent(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 6, 8, 25, 23, FUTURE["metal_dark"])
    for x in range(8, 24, 4):
        rect(draw, x, 9, x + 1, 22, FUTURE["metal_light"])
    return tile


def future_server(rng):
    tile = make_tile(FUTURE["metal_dark"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 8, 5, 23, 26, FUTURE["metal"])
    for y in (8, 12, 16, 20):
        rect(draw, 11, y, 14, y + 1, FUTURE["neon"])
        rect(draw, 17, y, 20, y + 1, FUTURE["warning"])
    return tile


def future_oil(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 6, 11, 25, 20, FUTURE["oil"])
    rect(draw, 10, 8, 21, 11, FUTURE["oil"])
    rect(draw, 10, 20, 19, 22, FUTURE["oil"])
    return tile


def future_emitter(rng):
    tile = future_metal(rng)
    draw = ImageDraw.Draw(tile)
    rect(draw, 10, 10, 21, 21, FUTURE["warning"])
    rect(draw, 13, 13, 18, 18, FUTURE["neon"])
    rect(draw, 15, 5, 16, 26, FUTURE["neon_soft"])
    rect(draw, 5, 15, 26, 16, FUTURE["neon_soft"])
    return tile


def future_transition(rng):
    tile = make_tile(FUTURE["metal"])
    draw = ImageDraw.Draw(tile)
    rect(draw, 0, 17, TILE - 1, TILE - 1, FUTURE["hazard_dark"])
    rect(draw, 0, 15, TILE - 1, 16, FUTURE["hazard"])
    speckle(draw, rng, [FUTURE["metal_dark"], FUTURE["metal_light"], FUTURE["rust"]], 36, 1, 2)
    return tile


def build_sheet(name, builders):
    sheet = Image.new("RGBA", (TILE * COLS, TILE * ROWS), (0, 0, 0, 0))
    for index, builder in enumerate(builders):
        rng = random.Random(f"{name}-{index}")
        tile = builder(rng)
        x = (index % COLS) * TILE
        y = (index // COLS) * TILE
        sheet.paste(tile, (x, y))
    return sheet


def save_manifest(path, tile_names):
    lines = [f"{index:02d}: {name}" for index, name in enumerate(tile_names)]
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    prehistoric_tiles = [
        ("grass", prehistoric_grass),
        ("dirt", prehistoric_dirt),
        ("path", prehistoric_path),
        ("stone", prehistoric_stone),
        ("mud", prehistoric_mud),
        ("bones", prehistoric_bones),
        ("firepit", prehistoric_firepit),
        ("cliff", prehistoric_cliff),
        ("bush", prehistoric_bush),
        ("stump", prehistoric_stump),
        ("fossil", prehistoric_fossil),
        ("water", prehistoric_water),
        ("totem", prehistoric_totem),
        ("transition", prehistoric_transition),
        ("grass_alt", prehistoric_grass),
        ("stone_alt", prehistoric_stone),
    ]
    future_tiles = [
        ("metal_floor", future_metal),
        ("circuit_floor", future_circuit),
        ("neon_grid", future_grid),
        ("hazard", future_hazard),
        ("rusted_floor", future_rust),
        ("robot_wreckage", future_wreckage),
        ("energy_core", future_energy),
        ("conveyor", future_conveyor),
        ("block", future_block),
        ("glass_panel", future_glass),
        ("vent", future_vent),
        ("server_rack", future_server),
        ("oil_spill", future_oil),
        ("emitter", future_emitter),
        ("transition", future_transition),
        ("metal_alt", future_metal),
    ]

    prehistoric_sheet = build_sheet("prehistoric", [builder for _, builder in prehistoric_tiles])
    future_sheet = build_sheet("future", [builder for _, builder in future_tiles])

    prehistoric_sheet.save(OUT_DIR / "prehistoric_tileset_32.png")
    future_sheet.save(OUT_DIR / "future_tileset_32.png")
    save_manifest(OUT_DIR / "prehistoric_tileset_32.txt", [name for name, _ in prehistoric_tiles])
    save_manifest(OUT_DIR / "future_tileset_32.txt", [name for name, _ in future_tiles])


if __name__ == "__main__":
    main()
