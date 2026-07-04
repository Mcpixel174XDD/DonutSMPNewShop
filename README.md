# SimpleShop

A configurable `/shop` GUI plugin for Spigot/Paper 1.20+ servers, using Vault
for the economy (works with EssentialsX Economy, or any other Vault-compatible
economy plugin).

## Features
- `/shop` opens a paginated chest GUI (54 slots/page) listing every item from `config.yml`
- Left-click an item to **buy** it, Shift-click to **sell** it back
- Prices, item names, and buy/sell amounts are fully configurable per item
- `/shopadmin reload` reloads `config.yml` without restarting the server
- Automatically skips any material name in the config that doesn't exist on
  your server version (logs a warning instead of crashing)

## Requirements
- A Spigot or Paper server, version 1.20 or newer
- [Vault](https://www.spigotmc.org/resources/vault.34315/) installed
- An economy plugin that hooks into Vault (e.g. **EssentialsX**, **CMI**, etc.)
- Java 17+ and [Maven](https://maven.apache.org/download.cgi) to build the plugin

## How to build the .jar

I couldn't compile this into a `.jar` for you directly because doing so
requires downloading the Spigot API and Vault API libraries from the internet,
and the environment I built this in has no network access. Building it
yourself takes about 30 seconds:

1. Install Maven if you don't have it:
   - Windows: `choco install maven` (or download from the link above)
   - Mac: `brew install maven`
   - Linux: `sudo apt install maven`
2. Open a terminal in this project folder (the one containing `pom.xml`)
3. Run:
   ```
   mvn clean package
   ```
4. The compiled plugin will appear at `target/SimpleShop.jar`

Alternatively, open the folder as a Maven project in IntelliJ IDEA or Eclipse
and use the built-in "Maven > package" action — same result, no terminal needed.

## Installation
1. Put `SimpleShop.jar` and `Vault.jar` (and your economy plugin) in your
   server's `plugins/` folder
2. Start/restart the server
3. Edit `plugins/SimpleShop/config.yml` to add, remove, or reprice items
4. Run `/shopadmin reload` in-game (or as console) to apply changes without restarting

## Configuring items
Each entry in `config.yml` under `items:` looks like this:

```yaml
  - material: COPPER_INGOT   # Bukkit Material enum name
    name: "&fCopper Ingot"   # Display name (supports & color codes)
    buy-price: 18            # Cost to buy `amount` of this item (-1 disables buying)
    sell-price: 6            # Payout for selling `amount` of this item (-1 disables selling)
    amount: 1                # Quantity per transaction
```

Add as many entries as you like — the GUI automatically paginates 45 items
per page with Previous/Next arrows in the bottom row.

## Permissions
- `simpleshop.use` — allows `/shop` (default: everyone)
- `simpleshop.admin` — allows `/shopadmin reload` (default: server operators)
