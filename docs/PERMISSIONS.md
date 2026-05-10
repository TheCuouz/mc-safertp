# Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `safertp.use` | `true` | Use `/rtp` |
| `safertp.admin` | `op` | `/rtp reload`, `/rtp other <player>` |
| `safertp.world.<name>` | `op` | Use RTP in the named world |
| `safertp.bypass.cooldown` | `op` | Skip cooldown entirely |
| `safertp.bypass.cost` | `op` | Skip economy cost |
| `safertp.cooldown.<seconds>` | — | Override cooldown to `<seconds>` (lowest granted value wins) |

## Cooldown Tiers Example

Grant `safertp.cooldown.60` to VIP rank for a 60-second cooldown instead of the default 300.
Multiple nodes can be granted; the lowest value applies.
