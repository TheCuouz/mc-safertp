# Changelog

All notable changes to SafeRTP are documented in this file. The format is based
on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project
follows semantic versioning.

## [1.1.0] - 2026-05-14

### Added

- **`/rtp back`** — Undo your last random teleport and return to the location
  you were standing on before the RTP fired. The pre-teleport location is held
  in memory only (no database hit) and expires after a configurable TTL.
- **New permissions**
  - `safertp.back` (default `true`) — grants access to `/rtp back`.
  - `safertp.back.nocooldown` (default `op`) — bypasses the back cooldown.
- **New config section** (`config.yml`):
  ```yaml
  back:
    enabled: true
    ttl-seconds: 300
    cooldown-seconds: 60
  ```
- **New messages keys** (`messages.yml`): `back-success`, `back-no-location`,
  `back-on-cooldown`, `back-no-permission`.
- Console boot banner now shows a `Back` hook badge when the feature is
  enabled.
- TTS-Studio SDK integration polished — chat prefix and ConsoleBanner reused
  across the suite.

### Changed

- `CooldownManager` now supports multiple independent cooldown buckets per
  player (`/rtp` and `/rtp back` cooldown independently). The legacy
  single-bucket API is preserved for backwards compatibility.
- `config.yml` bumped to `config-version: 2`. Existing configs continue to
  work — the `back:` section is added on first load with defaults.

### Internal

- New `BackLocationStore` (in-memory, TTL-bounded, thread-safe via
  `ConcurrentHashMap`) with injectable clock for deterministic tests.
- Periodic `purgeExpired()` sweep scheduled every 60 s to keep the map small.

## [1.0.0] - 2026-04-29

### Added

- Initial public release on SpigotMC.
- Annular distribution finder (min/max radius per world) to avoid spawn-point
  clustering.
- Per-world configuration in `worlds.yml`: enable flag, radius range, biome
  whitelist/blacklist, max attempts, Vault cost.
- Warmup countdown (title-based, cancels on move/damage) with `WarmupManager`
  and `WarmupListener`.
- Permission-resolved cooldowns via `safertp.cooldown.<n>` tiers (lower wins).
- Vault economy integration for paid teleports.
- PlaceholderAPI expansion exposing `%safertp_cooldown%` and
  `%safertp_can_use%`.
- Admin commands `/rtp other <player>` and `/rtp reload`.
- bStats metrics (plugin ID 12346).
