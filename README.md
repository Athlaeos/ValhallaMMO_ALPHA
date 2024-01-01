# ValhallaMMO
Alpha source code for ValhallaMMO, a very large configurable skills/leveling plugin for minecraft 

ALPHA IS OUTDATED! The plugin is unstable and unbalanced, and will no longer be maintained. Please use the Beta versions of ValhallaMMO going forward. 

## Getting started
1. Download the jar off of spigot: https://www.spigotmc.org/resources/valhallammo-early-alpha-the-most-advanced-free-skills-leveling-plugin.94921/
2. Place the jar in your server's "plugins" folder
3. (re)start the server
4. If you are playing on Spigot(not forks like Paper or Purpur), stop the server and set is_spigot to _true_ in config.yml, then start the server again
(it is still recommended to use Paper or Purpur not just for plugin functionality and general server performance)

The following permissions are most important for players to have:
* _valhalla.skills_ is required to access the player's skill tree. This is the most important permission to have
* _valhalla.help_ is required to access the _/valhalla help_ command
* _valhalla.profile_ is required to access and read the player's skill profiles. This is not very important, but it's nice to have
* _valhalla.createparty_ is required to be able to create parties

## Other important points
1. It completely overhauls the damage system with armor, armor toughness, and enchantments. This may be modified in config.yml
2. It introduces a completely new way of crafting that might be difficult for some players to understand, so they're given a short book on join introducing this new system
3. It disables vanilla crafting recipes for tools and armor by default in favor of the plugin's custom progression, but you can disable it through config.yml if wanted. 
4. The brewing system has been changed completely, so ValhallaMMO will not work with plugins interacting with brewing either.

## Known issues
1. The API "Unite", which purpose is to provide a shared party system for plugins like Quests and DungeonsXL, throws errors on PlayerMoveEvents while ValhallaMMO is installed. The cause is unknown.
2. Shift-right clicking items into an active brewing stand deletes the item.
