> 🌐 **English** · [Español](es/Installation.md)

# 📦 Installation

This page walks you through getting SafeRTP — part of the **TTS-Studio** plugin suite — running on your Paper server from zero to operational.

---

## 🔧 Requirements

| Software | Minimum Version | Required? |
|----------|----------------|-----------|
| [Paper](https://papermc.io/) | **1.21.x** (tested on 1.21.10) | ✅ Yes |
| Java | **21** | ✅ Yes |
| [Vault](https://www.spigotmc.org/resources/vault.34315/) + economy plugin | latest | ⚡ Recommended |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | 2.11.6+ | ⚡ Optional |

> ⚠️ **Warning:** SafeRTP uses Paper-specific APIs (`PaperLib` async chunks, Adventure titles). Spigot, Purpur without Paper core, or Folia are **not** supported (`folia-supported: false`).

> ⚠️ **Warning:** Java 21 is the minimum. Running on Java 17 will cause the plugin to fail to load.

---

## 🚀 Step-by-Step Installation

### Step 1 — Download the JAR

Purchase and download `safertp-1.0.0.jar` from BuiltByBit. Always use the official download — do not use cracked versions.

### Step 2 — Deploy the JAR

```bash
# Copy the JAR into your plugins folder
cp safertp-1.0.0.jar /your/server/plugins/

# Your plugins directory should now contain:
# plugins/
# ├── SafeRTP-1.0.0.jar
# ├── Vault.jar          (recommended)
# └── ...other plugins...
```

### Step 3 — First Start

Start or restart your server. SafeRTP generates its configuration automatically and prints the TTS-Studio boot banner:

```
╔══════════════════════════════════════╗
║        T T S - S T U D I O           ║
╚══════════════════════════════════════╝
   SafeRTP v1.0.0
   1 world(s) · Vault ✓ · PAPI ✓ · ready in 87ms
```

```
plugins/SafeRTP/
├── config.yml        ← main configuration (currently minimal)
├── worlds.yml        ← per-world radius, biome filters, cost
└── messages.yml      ← player-facing strings (MiniMessage)
```

### Step 4 — (Optional) Install Vault

If you want to charge players for each `/rtp`, install Vault plus any compatible economy plugin (EssentialsX, CMI, etc.):

```bash
# Drop Vault.jar and your economy plugin into plugins/
# Restart the server
# SafeRTP will auto-detect Vault on next enable:
[SafeRTP] Vault economy hooked.
```

Then set `cost: <amount>` per world in `worlds.yml` — see [Configuration](Configuration).

### Step 5 — (Optional) Install PlaceholderAPI

If you want cooldown placeholders on scoreboards/TAB/chat:

```bash
# Drop PlaceholderAPI.jar into plugins/
# Restart. SafeRTP auto-registers its expansion:
[SafeRTP] PlaceholderAPI expansion registered.
```

No `ecloud` download required — the expansion ships **inside** the SafeRTP jar.

### Step 6 — Tune & Reload

Edit `worlds.yml` to set sensible radii and a biome blacklist for *your* world (see [Configuration](Configuration)), then:

```
/rtp reload
```

No server restart needed for config changes.

---

## ✅ Post-Install Checklist

- [ ] JAR is in `plugins/` folder
- [ ] Server restarted at least once to generate configs
- [ ] TTS-Studio boot banner visible in console
- [ ] `worlds.yml` reviewed — `min-radius` / `max-radius` make sense for your map
- [ ] Vault detected if `cost` is set *(optional)*
- [ ] PlaceholderAPI detected if you use scoreboards *(optional)*
- [ ] Permission groups configured in your permissions plugin
- [ ] Test command run: `/rtp` as a non-OP player

---

## 🔄 Updating SafeRTP

1. Stop the server
2. Replace the old JAR with the new one (config files are preserved on disk)
3. Start the server
4. Run `/rtp reload` if prompted to re-read configs

> 💡 **Tip:** Back up `plugins/SafeRTP/` before updating, especially if you've customized `messages.yml` for translation.

---

## 🗑️ Uninstalling

1. Remove `SafeRTP-*.jar` from `plugins/`
2. Optionally delete `plugins/SafeRTP/` to remove all configs

SafeRTP stores no database — all cooldowns are in-memory only and cleared on shutdown.

---

## 🆘 Troubleshooting

| Symptom | Likely Cause | Fix |
|---------|-------------|-----|
| Plugin not loading | Java < 21 | Upgrade JRE to Java 21+ |
| `Folia is not supported` | Running on Folia | Use Paper 1.21.x — Folia support is on the roadmap |
| No TTS-Studio banner on enable | Paper version too old | Use Paper 1.21.x (tested on 1.21.10) |
| `Vault economy hooked.` missing | Vault not installed or no economy provider | Install Vault + an economy plugin (EssentialsX/CMI) |
| Players keep landing in oceans | Biome blacklist not applied | Confirm `biome-blacklist` in `worlds.yml`, then `/rtp reload` |
| `No se encontró una ubicación segura` repeatedly | `max-attempts` too low, or radius is mostly water/lava | Increase `max-attempts`, widen blacklist, or move center coords |
| Cooldown not respected | Player has `safertp.bypass.cooldown` | Revoke the permission for non-staff |
| Placeholders show `%safertp_...%` | PlaceholderAPI not installed | Install PAPI; the expansion is built-in |

---

🏠 [Home](Home) · ⚙️ [Configuration](Configuration) · 🎮 [Commands & Permissions](Commands-and-Permissions)
