# SafeRTP

Random teleport plugin for Paper 1.21.4. Teleports players to safe, configurable locations within world boundaries.

## Features

- Annular distribution (avoids spawn-point clustering)
- Per-world configuration: radius, biome whitelist/blacklist, cost, max attempts
- Warmup countdown (title-based, cancels on move/damage)
- Per-permission cooldown tiers (`safertp.cooldown.<n>`)
- Vault economy cost support
- PlaceholderAPI integration
- Admin commands: `/rtp reload`, `/rtp other <player>`

## Requirements

- Paper 1.21.4+
- Java 21+
- Optional: Vault (economy costs), PlaceholderAPI

## Installation

Drop `safertp-1.0.0.jar` into your `plugins/` folder and restart.

## Quick Start

```yaml
# plugins/SafeRTP/worlds.yml
cooldown-default: 300
warmup-seconds: 5

worlds:
  world:
    enabled: true
    min-radius: 1000
    max-radius: 10000
    biome-blacklist:
      - OCEAN
      - DEEP_OCEAN
    max-attempts: 50
    cost: 0
```

## Commands & Permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/rtp` | Teleport to a random safe location | `safertp.use` | true |
| `/rtp [world]` | RTP in a specific world | `safertp.use` | true |
| `/rtp other <player>` | Force RTP another player | `safertp.other` | op |
| `/rtp reload` | Reload config + worlds | `safertp.admin` | op |

**Cooldown bypass:** grant `safertp.cooldown.<n>` (e.g. `safertp.cooldown.60`) to set a player's cooldown to *n* seconds. Lower node wins. `safertp.cooldown.0` = no cooldown.

## PlaceholderAPI

| Placeholder | Value |
|---|---|
| `%safertp_cooldown%` | Seconds until next `/rtp` (0 if ready) |
| `%safertp_can_use%` | `true` / `false` |

## Documentation

- [CONFIG.md](docs/CONFIG.md) — all configuration options
- [PERMISSIONS.md](docs/PERMISSIONS.md) — permission nodes
- [PLACEHOLDERS.md](docs/PLACEHOLDERS.md) — PlaceholderAPI variables
- [CHANGELOG.md](docs/CHANGELOG.md)
