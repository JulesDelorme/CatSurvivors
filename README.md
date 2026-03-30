# Cat Survivors

Démo LibGDX d'un survivor-like 2D en Java où un chat traverse les époques.

## Contenu actuel

- menu principal avec sélection de map
- map 1 `Préhistoire`
- map 2 `Futur / Robots`
- terrain quasi infini généré autour du joueur
- progression persistante du déblocage de la map 2 via `Preferences`
- combat auto type Survivor
- XP, niveaux, choix de 3 upgrades
- 4 armes: `Canon à poils`, `Spray de pierres`, `Dards d'os`, `Griffes orbitales`
- 5 passifs: vitesse, dégâts, cadence, aimant XP, vitalité
- écrans de victoire, défaite et déblocage

## Contrôles

- déplacement: `WASD`, `ZQSD` ou flèches
- pause: `Échap`
- choisir un upgrade: `1`, `2`, `3` ou clic
- menu:
  - `1` lance la Préhistoire
  - `2` lance le Futur s'il est débloqué
  - `Entrée` lance la Préhistoire

## Lancer le projet

```bash
./gradlew clean build
./gradlew lwjgl3:run
```

Le déblocage de la map 2 est stocké localement par LibGDX dans les préférences de l'application.

## Architecture

Le code est volontairement modulaire sans partir sur un ECS complexe.

- [`Main`](./core/src/main/java/io/github/some_example_name/Main.java): racine `Game`
- [`context`](./core/src/main/java/io/github/some_example_name/context): assets partagés, navigation, progression persistante
- [`screen`](./core/src/main/java/io/github/some_example_name/screen): `MenuScreen`, `GameScreen`, `UnlockScreen`, `EndScreen`
- [`game/session`](./core/src/main/java/io/github/some_example_name/game/session): boucle de partie, état runtime, XP, vagues, victoire/défaite
- [`game/stage`](./core/src/main/java/io/github/some_example_name/game/stage): définitions de maps, layouts, tuning par époque
- [`TerrainGenerator`](./core/src/main/java/io/github/some_example_name/game/stage/TerrainGenerator.java): génération déterministe du terrain quasi infini
- [`game/weapon`](./core/src/main/java/io/github/some_example_name/game/weapon): système d'armes extensible
- [`game/upgrade`](./core/src/main/java/io/github/some_example_name/game/upgrade): cartes d'amélioration
- [`render`](./core/src/main/java/io/github/some_example_name/render): rendu map, entités, HUD et overlays

## Assets

- chat animé:
  - `assets/characters/cat/cat_idle_sheet.png`
  - `assets/characters/cat/cat_run_sheet.png`
  - `assets/characters/cat/cat_jump_sheet.png`
  - `assets/characters/cat/cat_fall_sheet.png`
- tilesets:
  - `assets/tilesets/prehistoric_tileset_32.png`
  - `assets/tilesets/future_tileset_32.png`
- banque d'icônes:
  - `assets/icons/raven_fantasy_32.png`
- sprites web intégrés:
  - `assets/sprites/prehistory/kenney_desert_enemies_24.png`
  - `assets/sprites/future/robot_blue.png`
  - `assets/sprites/future/robot_green.png`
  - `assets/sprites/future/robot_red.png`
  - `assets/sprites/future/robot_yellow.png`
- licences:
  - `assets/licenses/kenney_desert_shooter_pack_license.txt`
  - `assets/licenses/kenney_robot_pack_license.txt`

## Sources web utilisées

- Kenney Desert Shooter Pack, licence CC0:
  - https://kenney.nl/assets/desert-shooter-pack
- Kenney Robot Pack, licence CC0:
  - https://kenney.nl/assets/robot-pack

## Ajouter une nouvelle époque

1. Ajouter une nouvelle valeur dans `StageId`.
2. Créer une nouvelle définition dans `StageLibrary`.
3. Ajouter un tileset et la palette HUD associée dans `GameAssets` si nécessaire.
4. Déclarer les nouveaux archétypes d'ennemis dans la définition de stage.
5. Brancher le déblocage et le menu si la nouvelle époque doit être sélectionnable.

## Ajouter une nouvelle arme

1. Ajouter une valeur dans `WeaponType`.
2. Créer une implémentation `Weapon` ou étendre `ProjectileWeapon` / `OrbitWeapon`.
3. L'enregistrer dans `GameSession`.
4. Ajouter son icône dans `GameAssets` et son rendu dans `EntityRenderer` si besoin.

## Ajouter un nouvel ennemi

1. Définir un `EnemyArchetype`.
2. L'ajouter aux pondérations de spawn du stage concerné.
3. Ajuster le rendu placeholder dans `EntityRenderer` si une silhouette spécifique est nécessaire.
