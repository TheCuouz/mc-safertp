# 🌍 SafeRTP Wiki

```
   ███████╗ █████╗ ███████╗███████╗██████╗ ████████╗██████╗
   ██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗╚══██╔══╝██╔══██╗
   ███████╗███████║█████╗  █████╗  ██████╔╝   ██║   ██████╔╝
   ╚════██║██╔══██║██╔══╝  ██╔══╝  ██╔══██╗   ██║   ██╔═══╝
   ███████║██║  ██║██║     ███████╗██║  ██║   ██║   ██║
   ╚══════╝╚═╝  ╚═╝╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝
```

**Safe random teleportation for Paper 1.21.x (tested on 1.21.10)** — async chunk loading, multi-pass safety checks, no more lava deaths.

> 🏛️ Part of the **TTS-Studio** plugin suite — premium server-side tooling, crafted for production.

---

## ✨ Feature Highlights

| Feature | Description |
|---------|-------------|
| 🎯 **Annulus-Based Distribution** | Uniform random points between `min-radius` and `max-radius` around a configurable center |
| 🛡️ **Multi-Layer Safety Checks** | Validates floor, head clearance, lava/fire/cactus/void, world height bounds, and biome filters |
| ⚙️ **Per-World Configuration** | Radius, center, biome white/blacklist, max attempts, and cost — all configurable per world |
| ⏱️ **Cancellable Warmup** | Configurable countdown shown via Title API; cancels on movement or damage |
| 🕒 **Permission-Based Cooldowns** | Default cooldown plus per-rank overrides via `safertp.cooldown.<seconds>` permissions |
| 💰 **Vault Economy** | Optional cost per RTP, deducted only on successful teleport — bypass via permission |
| 📊 **PlaceholderAPI** | Live cooldown remaining and ready-to-use placeholders |
| 🚀 **Async Chunk Loading** | Uses PaperLib to load candidate chunks off-thread — no TPS spikes |
| 🎨 **TTS-Studio Branding** | Framed startup banner and uniform chat prefix across the suite |

---

## 🎭 In-server Experience

SafeRTP wears the TTS-Studio house style — a signature you'll recognize across every plugin in the suite.

### 🟣 Console Boot Banner

On enable, SafeRTP prints a framed banner in TTS-Studio purple (`#C879FF`), followed by the plugin line and a live status readout:

```
╔══════════════════════════════════════╗
║        T T S - S T U D I O           ║
╚══════════════════════════════════════╝
   SafeRTP v1.0.0
   1 world(s) · Vault ✓ · PAPI ✓ · ready in 87ms
```

The status line reports — at a glance — how many worlds are RTP-enabled, whether Vault and PlaceholderAPI hooked successfully, and how long enable took.

### 💬 Chat Prefix

Every plugin-voice message SafeRTP sends to a player carries the unified suite prefix:

```
◈ SafeRTP: ¡Teletransportado a world (1234, 72, -5678)!
```

| Token | Color | Meaning |
|-------|-------|---------|
| `◈` | `gray` | TTS-Studio glyph — shared across all suite plugins |
| `SafeRTP` | teal `#1ABC9C` | Plugin name in its accent color (7 chars, full name kept) |
| `:` | `dark_gray` | Separator |

> 📝 **Note:** SafeRTP does **not** include a `ConfirmGui`. The `/rtp` flow has a built-in warmup window during which the player can cancel by moving — that *is* the confirmation. No extra GUI prompt is needed.

---

## ⚡ Quick Start

```bash
# 1. Drop the jar into your plugins folder
cp safertp-1.0.0.jar plugins/

# 2. Start (or restart) the server — config.yml, worlds.yml and messages.yml
#    are generated automatically.

# 3. (Optional) Install Vault if you want to charge per RTP

# 4. Edit plugins/SafeRTP/worlds.yml — tune the radius and biome blacklist

# 5. Apply changes without restart:
/rtp reload
```

> 💡 **Tip:** Defaults teleport between 1,000 and 10,000 blocks from spawn in the overworld, with ocean biomes blacklisted — sensible for most survival servers.

---

## 🧭 Wiki Navigation

| Page | Contents |
|------|----------|
| 📦 [Installation](Installation) | Requirements, JAR deployment, first-run checklist |
| ⚙️ [Configuration](Configuration) | Full `config.yml`, `worlds.yml`, and `messages.yml` reference |
| 🎮 [Commands & Permissions](Commands-and-Permissions) | All commands, permissions table, cooldown tiers |
| 📊 [PlaceholderAPI](PlaceholderAPI) | All placeholders for scoreboards, TAB, chat formatting |

---

## 📋 Requirements

| Requirement | Version | Notes |
|-------------|---------|-------|
| Paper | 1.21.x (tested on 1.21.10) | Spigot is **not** supported |
| Java | 21+ | Required |
| Vault | latest + an economy plugin | Optional — enables `cost` per RTP |
| PlaceholderAPI | 2.11.6+ | Optional — enables placeholders |

---

*SafeRTP — part of the **TTS-Studio** plugin suite · safer wilderness teleports since 2024 · [BuiltByBit](https://builtbybit.com)*
