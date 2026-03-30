# CatSurvivors Agents

Project target: a 2D top-down survivor prototype in Java with libGDX.

Current MVP:
- one controllable cat
- endless enemy waves
- contact damage
- survival timer
- simple HUD
- no external art required for the first playable build

## Agent 1: Director

Mission:
- keep scope tight
- decide integration order
- protect file ownership boundaries
- merge subsystem work into one playable build

Owns:
- `AGENTS.md`
- `README.md`
- `core/src/main/java/io/github/some_example_name/Main.java`
- screen wiring and cross-system integration

Rules:
- reject features that slow down the first playable version
- prefer simple rectangles/circles before asset pipelines
- keep package structure small and explicit

## Agent 2: Core Loop

Mission:
- implement the simulation loop and shared game state
- own player movement, world bounds, timers, and update order

Owns:
- `core/src/main/java/io/github/some_example_name/game/`
- `core/src/main/java/io/github/some_example_name/game/model/`
- `core/src/main/java/io/github/some_example_name/game/state/`

Expected responsibilities:
- fixed or semi-fixed update flow
- player position, speed, health
- survival timer
- game over state

## Agent 3: Enemies And Combat

Mission:
- implement enemy spawning, pursuit behavior, and collision-driven damage

Owns:
- `core/src/main/java/io/github/some_example_name/game/enemy/`
- `core/src/main/java/io/github/some_example_name/game/combat/`

Expected responsibilities:
- spawn cadence
- enemy data and movement toward player
- hit detection
- damage cooldowns and cleanup of dead entities

## Agent 4: Rendering And HUD

Mission:
- make the prototype readable and immediately playable with placeholder visuals

Owns:
- `core/src/main/java/io/github/some_example_name/screen/`
- `core/src/main/java/io/github/some_example_name/render/`
- `core/src/main/java/io/github/some_example_name/ui/`
- current `FirstScreen` replacement

Expected responsibilities:
- camera and viewport
- ShapeRenderer or texture-based placeholder rendering
- HUD for HP, timer, and enemy count
- pause and restart affordances if needed

## Agent 5: Feel And Tuning

Mission:
- tune the prototype so it becomes fun quickly without changing the architecture

Owns:
- `core/src/main/java/io/github/some_example_name/config/`
- balancing constants and progression curves

Expected responsibilities:
- move speed
- enemy speed and spawn ramp
- health values
- arena size and pressure curve

## Agent 6: Platform And Build

Mission:
- keep desktop launch, Gradle, and assets stable while gameplay evolves

Owns:
- `build.gradle`
- `gradle.properties`
- `lwjgl3/build.gradle`
- `lwjgl3/src/main/java/io/github/some_example_name/lwjgl3/`
- `assets/`

Expected responsibilities:
- run/build reliability
- launcher settings
- debug-friendly desktop defaults
- asset folder conventions

## Working Agreement

- Do not edit another agent's files without an explicit handoff.
- Prefer adding new packages over growing `FirstScreen` into a god object.
- Keep the first playable version asset-light and code-heavy.
- Every gameplay change should still allow `./gradlew clean build` to pass.
- When in doubt, optimize for a playable loop over abstraction quality.

## First Delivery Order

1. Director defines the package layout and replaces `FirstScreen` with a real game screen.
2. Core Loop creates player/world/timer state.
3. Enemies And Combat adds spawning, pursuit, and damage.
4. Rendering And HUD makes the prototype visible and controllable.
5. Feel And Tuning adjusts numbers only after the loop is playable.
6. Platform And Build keeps desktop run/build stable throughout.
