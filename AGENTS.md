# Cat Survivors Agents

Projet: survivor-like 2D Java + LibGDX avec chat traversant les époques.

## Director

Mission:
- maintenir la cohérence de la démo
- protéger le flux `Menu -> Game -> Unlock/End`
- arbitrer les ajouts pour préserver jouabilité et lisibilité

Possède:
- `README.md`
- `AGENTS.md`
- `core/src/main/java/io/github/some_example_name/Main.java`
- `core/src/main/java/io/github/some_example_name/context/`
- `core/src/main/java/io/github/some_example_name/screen/`

## Session And Gameplay

Mission:
- gérer la boucle survivor complète
- porter l'état runtime d'une partie
- faire vivre XP, niveaux, armes, passifs, vagues, victoire et défaite

Possède:
- `core/src/main/java/io/github/some_example_name/game/session/`
- `core/src/main/java/io/github/some_example_name/game/upgrade/`
- `core/src/main/java/io/github/some_example_name/game/weapon/`

## Stages And Enemies

Mission:
- définir les maps et la montée en difficulté
- gérer les archétypes d'ennemis, le terrain quasi infini et les final waves

Possède:
- `core/src/main/java/io/github/some_example_name/game/stage/`
- `core/src/main/java/io/github/some_example_name/game/Enemy.java`
- `core/src/main/java/io/github/some_example_name/game/EnemyArchetype.java`

## Player And Combat Models

Mission:
- garder des modèles runtime simples, lisibles et réutilisables
- éviter que les données de combat repartent dans les écrans

Possède:
- `core/src/main/java/io/github/some_example_name/game/Player.java`
- `core/src/main/java/io/github/some_example_name/game/Projectile.java`
- `core/src/main/java/io/github/some_example_name/game/ExperienceOrb.java`
- `core/src/main/java/io/github/some_example_name/game/OrbitBlade.java`
- `core/src/main/java/io/github/some_example_name/game/CatAnim.java`
- `core/src/main/java/io/github/some_example_name/game/PassiveType.java`
- `core/src/main/java/io/github/some_example_name/game/WeaponType.java`

## Rendering And HUD

Mission:
- conserver un rendu lisible et fun avec placeholders propres
- centraliser carte, entités, HUD et overlays

Possède:
- `core/src/main/java/io/github/some_example_name/render/`
- `core/src/main/java/io/github/some_example_name/ui/`

## Platform And Assets

Mission:
- garder le build desktop fiable
- maintenir les conventions d'assets

Possède:
- `build.gradle`
- `lwjgl3/build.gradle`
- `lwjgl3/src/main/java/io/github/some_example_name/lwjgl3/`
- `assets/`

## Règles

- garder la boucle jouable avant d'ajouter du détail
- éviter les écrans ou systèmes qui recodent la logique métier
- ne pas refaire un `FirstScreen` monolithique
- toute feature doit conserver `./gradlew clean build`
