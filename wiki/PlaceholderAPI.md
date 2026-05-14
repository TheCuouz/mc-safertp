> 🌐 **English** · [Español](es/PlaceholderAPI.md)

# 📊 PlaceholderAPI Integration

SafeRTP exposes its cooldown state to PlaceholderAPI so you can drop it into scoreboards, TAB plugins, chat formatters, HUDs, GUI menus — anywhere PAPI is supported.

---

## ⚙️ Setup

### 1. Install PlaceholderAPI

```bash
# Drop PlaceholderAPI.jar into plugins/
# Restart the server. SafeRTP auto-registers its expansion:
[SafeRTP] PlaceholderAPI expansion registered.
```

> 💡 **Tip:** The SafeRTP expansion ships **inside** the plugin jar — you do **not** need `/papi ecloud download SafeRTP`. It registers automatically on `onEnable` if PAPI is present.

> ⚠️ **Warning:** If PlaceholderAPI is not installed, all `%safertp_...%` placeholders display literally as `%safertp_...%`.

### 2. Verify

```bash
/papi test <yourname> %safertp_cooldown%
# Returns the number of seconds remaining, or 0 if ready
```

---

## 📋 Placeholder Reference

| Placeholder | Returns | Example Output | Notes |
|-------------|---------|----------------|-------|
| `%safertp_cooldown%` | Seconds of cooldown remaining for the player | `42` | Returns `0` when ready |
| `%safertp_can_use%` | `true` if the player has no active cooldown, otherwise `false` | `true` | Useful for conditional displays |

### Detailed Descriptions

#### `%safertp_cooldown%`
Returns the integer number of seconds the player must wait before `/rtp` is usable again.

- Returns `0` when the cooldown has expired or was never set
- Updates live — refresh your scoreboard every second for a clean countdown
- Does **not** consider `safertp.bypass.cooldown` — staff with bypass will still see `0`

#### `%safertp_can_use%`
Returns the literal string `true` or `false`.

- `true` — no active cooldown (player can run `/rtp` right now, assuming permission)
- `false` — cooldown active
- Pair with a conditional placeholder plugin to swap a button colour or hide a GUI slot

---

## 💡 Usage Examples

### Scoreboard Line (FeatherBoard / CMI / AnimatedScoreboard)

```yaml
lines:
  - "&7RTP: &f%safertp_cooldown%s"
  - "&7Ready? &a%safertp_can_use%"
```

### TAB by NEZNAMY

```yaml
tablist-name: "%player_name% &8| &7RTP &e%safertp_cooldown%s"
```

### DeluxeMenus — Greyed-Out Button

```yaml
slot: 13
material: ENDER_PEARL
display_name: "&bRandom Teleport"
lore:
  - ""
  - "&7Cooldown: &e%safertp_cooldown%s"
view_requirement:
  requirements:
    can_use:
      type: string equals ignorecase
      input: "%safertp_can_use%"
      output: "true"
click_commands:
  - "[player] rtp"
```

### Conditional Chat (DeluxeChat)

```yaml
# Show a green dot if RTP is ready, red if on cooldown
prefix: "{condition: %safertp_can_use%=true}&a● &r|&c● &r}"
```

---

## 🔗 Compatible Plugins

| Plugin | Works With SafeRTP Placeholders |
|--------|---------------------------------|
| TAB by NEZNAMY | ✅ |
| FeatherBoard | ✅ |
| CMI | ✅ |
| AnimatedScoreboard | ✅ |
| DeluxeMenus | ✅ |
| DeluxeChat | ✅ |
| HolographicDisplays | ✅ (via PAPI bridge) |
| EssentialsX (chat format) | ✅ |

---

## 🛠️ Combining With Other PAPI Placeholders

The cooldown is most useful when blended with player and server placeholders:

```yaml
# A full status line for a survival HUD:
- "&7%player_name% &8| &7Balance: &a$%vault_eco_balance%"
- "&7RTP &7▸ &f%safertp_cooldown%s"
```

> 💡 **Tip:** Want to count *how many* players are currently on cooldown across the server? Combine `%safertp_can_use%` with `Player_list` extensions in your scoreboard plugin.

---

🏠 [Home](Home) · 📦 [Installation](Installation) · 🎮 [Commands & Permissions](Commands-and-Permissions) · ⚙️ [Configuration](Configuration)
