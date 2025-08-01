<div align="center">

# 🔨 VeinMiner

**Advanced Vein Mining Plugin for Minecraft**

[![GitHub release](https://img.shields.io/github/v/release/sw3do/mc-veinminer?style=for-the-badge)](https://github.com/sw3do/mc-veinminer/releases)
[![GitHub downloads](https://img.shields.io/github/downloads/sw3do/mc-veinminer/total?style=for-the-badge)](https://github.com/sw3do/mc-veinminer/releases)
[![GitHub stars](https://img.shields.io/github/stars/sw3do/mc-veinminer?style=for-the-badge)](https://github.com/sw3do/mc-veinminer/stargazers)
[![GitHub license](https://img.shields.io/github/license/sw3do/mc-veinminer?style=for-the-badge)](https://github.com/sw3do/mc-veinminer/blob/main/LICENSE)

*Mine entire veins of ores, logs, and other blocks with a single break!*

[📥 Download](https://github.com/sw3do/mc-veinminer/releases) • [📖 Wiki](https://github.com/sw3do/mc-veinminer/wiki) • [🐛 Issues](https://github.com/sw3do/mc-veinminer/issues) • [💬 Discussions](https://github.com/sw3do/mc-veinminer/discussions)

</div>

---

## ✨ Overview

VeinMiner is a high-performance, feature-rich plugin that allows players to mine entire veins of connected blocks with a single break. Perfect for survival servers, it maintains game balance while significantly improving the mining experience.

## Features

### 🚀 Core Features
- **Universal Compatibility**: Works on all Minecraft versions from 1.13 to 1.20+
- **High Performance**: Async processing with configurable batch sizes
- **Smart Algorithm**: Efficient connected block detection
- **Tool Durability**: Realistic durability consumption with Unbreaking support
- **Drop Management**: Configurable item dropping and collection

### ⚙️ Customization
- **Configurable Block Types**: Add/remove any blocks for vein mining
- **Tool Restrictions**: Define which tools can vein mine which blocks
- **Block Limits**: Set different limits for different block types
- **Distance Control**: Configurable search distance for connected blocks
- **Permission System**: Granular permissions for different features

### 🛡️ Protection Integration
- **WorldGuard**: Respects region protections
- **GriefPrevention**: Works with claim systems
- **Towny**: Honors town permissions
- **Factions**: Respects faction territories

### 🎮 User Experience
- **Toggle System**: Players can enable/disable vein mining
- **Sneaking Requirement**: Optional sneaking to activate
- **Tool Breaking Prevention**: Prevents accidental tool destruction
- **Colored Messages**: Beautiful formatted messages with hex color support
- **Action Bar/Title Support**: Modern UI feedback

## Installation

1. Download the latest release from the [Releases](https://github.com/sw3do/mc-veinminer/releases) page
2. Place the `VeinMiner.jar` file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/VeinMiner/config.yml`

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/veinminer` | Show help message | `veinminer.use` |
| `/veinminer toggle` | Toggle vein mining on/off | `veinminer.use` |
| `/veinminer status` | Check your vein miner status | `veinminer.use` |
| `/veinminer info` | Show plugin information | `veinminer.use` |
| `/veinminer reload` | Reload configuration | `veinminer.admin` |

**Aliases**: `/vm`, `/veins`

## Permissions

| Permission | Description | Default |
|------------|-------------|----------|
| `veinminer.use` | Allows using vein miner | `true` |
| `veinminer.admin` | Allows admin commands | `op` |
| `veinminer.bypass` | Bypasses all restrictions | `op` |
| `veinminer.blocks.<block>` | Permission for specific blocks | `true` |

### Block-Specific Permissions
You can set permissions for specific blocks:
- `veinminer.blocks.diamond_ore` - Permission to vein mine diamond ore
- `veinminer.blocks.oak_log` - Permission to vein mine oak logs
- etc.

## Configuration

The plugin comes with a comprehensive configuration file:

```yaml
# Enable or disable the plugin
enabled: true

# Maximum number of blocks that can be mined in one vein
max-blocks: 64

# Maximum distance to search for connected blocks
max-distance: 3

# Require player to be sneaking to activate vein mining
require-sneaking: true

# Require the correct tool for the block type
require-correct-tool: true

# Performance settings
performance:
  async-processing: true
  max-processing-time: 50
  batch-size: 10
```

### Supported Blocks (Default)

**Ores:**
- Coal Ore (+ Deepslate)
- Iron Ore (+ Deepslate)
- Gold Ore (+ Deepslate)
- Diamond Ore (+ Deepslate)
- Emerald Ore (+ Deepslate)
- Lapis Ore (+ Deepslate)
- Redstone Ore (+ Deepslate)
- Copper Ore (+ Deepslate)
- Nether Gold Ore
- Nether Quartz Ore
- Ancient Debris

**Logs:**
- All wood types (Oak, Birch, Spruce, Jungle, Acacia, Dark Oak, Mangrove, Cherry)

**Other:**
- Stone, Cobblestone, Deepslate
- Sand, Gravel, Dirt

### Supported Tools (Default)

**Pickaxes:** Wooden, Stone, Iron, Golden, Diamond, Netherite
**Axes:** Wooden, Stone, Iron, Golden, Diamond, Netherite
**Shovels:** Wooden, Stone, Iron, Golden, Diamond, Netherite

## Building from Source

### Requirements
- Java 8 or higher
- Maven 3.6+

### Build Steps
```bash
git clone https://github.com/sw3do/mc-veinminer.git
cd mc-veinminer
mvn clean package
```

The compiled JAR will be in the `target` folder.

## API Usage

Developers can integrate with VeinMiner:

```java
// Get the plugin instance
VeinMinerPlugin plugin = (VeinMinerPlugin) Bukkit.getPluginManager().getPlugin("VeinMiner");

// Check if a player has vein mining enabled
boolean enabled = plugin.getVeinMinerManager().hasVeinMinerEnabled(player);

// Toggle vein mining for a player
plugin.getVeinMinerManager().toggleVeinMiner(player);

// Check if a block can be vein mined
boolean canMine = plugin.getVeinMinerManager().canVeinMine(player, block, tool);
```

## Performance

VeinMiner is designed for high performance:

- **Async Processing**: Large veins are processed asynchronously
- **Batch Processing**: Blocks are processed in configurable batches
- **Smart Algorithms**: Efficient pathfinding and duplicate detection
- **Memory Efficient**: Minimal memory footprint
- **Configurable Limits**: Prevent server lag with customizable limits

## Compatibility

### Supported Minecraft Versions

#### ✅ Fully Supported Versions
- **1.20.4** - Latest stable release
- **1.20.3** - Full feature support
- **1.20.2** - Full feature support
- **1.20.1** - Full feature support
- **1.20** - Full feature support
- **1.19.4** - Full feature support
- **1.19.3** - Full feature support
- **1.19.2** - Full feature support
- **1.19.1** - Full feature support
- **1.19** - Full feature support
- **1.18.2** - Full feature support
- **1.18.1** - Full feature support
- **1.18** - Full feature support
- **1.17.1** - Full feature support
- **1.17** - Full feature support
- **1.16.5** - Full feature support
- **1.16.4** - Full feature support
- **1.16.3** - Full feature support
- **1.16.2** - Full feature support
- **1.16.1** - Full feature support
- **1.16** - Full feature support
- **1.15.2** - Full feature support
- **1.15.1** - Full feature support
- **1.15** - Full feature support
- **1.14.4** - Full feature support
- **1.14.3** - Full feature support
- **1.14.2** - Full feature support
- **1.14.1** - Full feature support
- **1.14** - Full feature support
- **1.13.2** - Full feature support
- **1.13.1** - Full feature support
- **1.13** - Minimum supported version

#### 🔮 Future Versions
- **1.21+** - Will be supported upon release
- Automatic compatibility with new block types
- Forward compatibility built-in

#### ❌ Unsupported Versions
- **1.12.2 and below** - Not supported due to API limitations
- **Snapshot versions** - May work but not officially supported

### Version-Specific Features

#### 1.20+ Features
- Cherry Log support
- All new deepslate ore variants
- Enhanced performance optimizations

#### 1.19+ Features
- Mangrove Log support
- Deep Dark block compatibility
- Ancient City integration

#### 1.18+ Features
- All Deepslate ore variants
- Extended world height support
- Cave generation compatibility

#### 1.17+ Features
- Copper ore support
- Deepslate block support
- Raw ore compatibility

#### 1.16+ Features
- Netherite tool support
- Nether gold ore
- Blackstone compatibility
- Hex color code support in messages

#### 1.15+ Features
- Honey block support
- Bee nest compatibility

#### 1.14+ Features
- All new stone variants
- Bamboo support
- Village and Pillage blocks

#### 1.13+ Features
- Aquatic Update blocks
- Kelp and seagrass support
- Coral block compatibility

### Server Software Compatibility
- **Spigot** - Full support
- **Paper** - Full support (recommended)
- **Purpur** - Full support
- **Tuinity** - Full support
- **Airplane** - Full support
- **Pufferfish** - Full support
- **CraftBukkit** - Basic support
- **Mohist** - Limited support
- **Magma** - Limited support
- **Arclight** - Limited support

### Java Version Requirements
- **Java 8** - Minimum requirement
- **Java 11** - Recommended
- **Java 17** - Fully supported
- **Java 21** - Fully supported

## Support

If you encounter any issues or have suggestions:

1. Check the [Issues](https://github.com/sw3do/mc-veinminer/issues) page
2. Create a new issue with detailed information
3. Include your server version, plugin version, and configuration

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. 🍴 Fork the repository
2. 🌿 Create a feature branch (`git checkout -b feature/amazing-feature`)
3. 💾 Commit your changes (`git commit -m 'Add amazing feature'`)
4. 📤 Push to the branch (`git push origin feature/amazing-feature`)
5. 🔄 Open a Pull Request

## 📊 Statistics

![GitHub repo size](https://img.shields.io/github/repo-size/sw3do/mc-veinminer)
![GitHub code size](https://img.shields.io/github/languages/code-size/sw3do/mc-veinminer)
![GitHub last commit](https://img.shields.io/github/last-commit/sw3do/mc-veinminer)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/sw3do/mc-veinminer)

---

<div align="center">

## 👨‍💻 Author

**sw3do**

[![GitHub](https://img.shields.io/badge/GitHub-sw3do-181717?style=for-the-badge&logo=github)](https://github.com/sw3do)
[![Profile Views](https://komarev.com/ghpvc/?username=sw3do&style=for-the-badge&color=blue)](https://github.com/sw3do)

*Made with ❤️ for the Minecraft community*

**⭐ If you found this project helpful, please give it a star!**

</div>