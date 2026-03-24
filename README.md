# Spawner Pickaxe

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Silk Touch X Spawners** 

## Current Features

- **Spawner Harvesting**: Use a Golden Pickaxe with Silk Touch X to drop a Spawner block.
- **NBT Preservation**: Dropped spawners retain their mob type (e.g., Zombie, Skeleton, Blaze).
- **Server-Side Only**: Can be installed on the server without requiring clients to have the mod.
- **Vanilla Compatible**: Vanilla clients can connect and use the features if the mod is on the server.
- **Configurable**

## How to Use

1. **Craft the Spawner Pickaxe**: Combine a **Golden Pickaxe** and a **Dragon Egg** (default ingredient) in a crafting table. This will create a specialized "Spawner Pickaxe" with Silk Touch X.
2. **Mine a Spawner**: Break any spawner block with your specialized pickaxe.
3. **Pick up and Relocate**: The spawner will drop as an item. Place it anywhere you like, and it will keep its original mob type!

## Configuration

The mod generates a configuration file at `config/spawner-pickaxe.json`. You can customize the following options:

- `recipeIngredient`: The item ID required to craft the Spawner Pickaxe (Default: `minecraft:dragon_egg`).
- `pickaxeDamage`: The amount of durability damage the Golden Pickaxe has when crafted into a Spawner Pickaxe (Default: `16`).

---

*This mod was developed with AI assistance. For bug reports or feature requests, please open an issue on GitHub.*
