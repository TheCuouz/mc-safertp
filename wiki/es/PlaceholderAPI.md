> 🌐 [English](../PlaceholderAPI.md) · **Español**

# 📊 Integración con PlaceholderAPI

SafeRTP expone su estado de enfriamiento a PlaceholderAPI para que puedas usarlo en marcadores, plugins de TAB, formateadores de chat, HUDs, menús de GUI — en cualquier lugar donde PAPI esté soportado.

---

## ⚙️ Configuración

### 1. Instala PlaceholderAPI

```bash
# Coloca PlaceholderAPI.jar en plugins/
# Reinicia el servidor. SafeRTP registra su expansión automáticamente:
[SafeRTP] PlaceholderAPI expansion registered.
```

> 💡 **Consejo:** La expansión de SafeRTP viene **dentro** del jar del plugin — **no** necesitas `/papi ecloud download SafeRTP`. Se registra automáticamente en `onEnable` si PAPI está presente.

> ⚠️ **Advertencia:** Si PlaceholderAPI no está instalado, todos los placeholders `%safertp_...%` se muestran literalmente como `%safertp_...%`.

### 2. Verifica

```bash
/papi test <tunombre> %safertp_cooldown%
# Devuelve el número de segundos restantes, o 0 si está listo
```

---

## 📋 Referencia de Placeholders

| Placeholder | Devuelve | Salida de Ejemplo | Notas |
|-------------|----------|-------------------|-------|
| `%safertp_cooldown%` | Segundos de enfriamiento restantes para el jugador | `42` | Devuelve `0` cuando está listo |
| `%safertp_can_use%` | `true` si el jugador no tiene enfriamiento activo, `false` en caso contrario | `true` | Útil para visualizaciones condicionales |

### Descripciones Detalladas

#### `%safertp_cooldown%`
Devuelve el número entero de segundos que el jugador debe esperar antes de que `/rtp` esté disponible de nuevo.

- Devuelve `0` cuando el enfriamiento ha expirado o nunca se estableció
- Se actualiza en vivo — refresca tu marcador cada segundo para una cuenta atrás limpia
- **No** tiene en cuenta `safertp.bypass.cooldown` — el staff con omisión igualmente verá `0`

#### `%safertp_can_use%`
Devuelve la cadena literal `true` o `false`.

- `true` — sin enfriamiento activo (el jugador puede ejecutar `/rtp` ahora mismo, asumiendo permiso)
- `false` — enfriamiento activo
- Combínalo con un plugin de placeholder condicional para cambiar el color de un botón u ocultar un slot de GUI

---

## 💡 Ejemplos de Uso

### Línea de Marcador (FeatherBoard / CMI / AnimatedScoreboard)

```yaml
lines:
  - "&7RTP: &f%safertp_cooldown%s"
  - "&7¿Listo? &a%safertp_can_use%"
```

### TAB by NEZNAMY

```yaml
tablist-name: "%player_name% &8| &7RTP &e%safertp_cooldown%s"
```

### DeluxeMenus — Botón en Gris

```yaml
slot: 13
material: ENDER_PEARL
display_name: "&bRandom Teleport"
lore:
  - ""
  - "&7Enfriamiento: &e%safertp_cooldown%s"
view_requirement:
  requirements:
    can_use:
      type: string equals ignorecase
      input: "%safertp_can_use%"
      output: "true"
click_commands:
  - "[player] rtp"
```

### Chat Condicional (DeluxeChat)

```yaml
# Muestra un punto verde si el RTP está listo, rojo si está en enfriamiento
prefix: "{condition: %safertp_can_use%=true}&a● &r|&c● &r}"
```

---

## 🔗 Plugins Compatibles

| Plugin | Funciona con Placeholders de SafeRTP |
|--------|--------------------------------------|
| TAB by NEZNAMY | ✅ |
| FeatherBoard | ✅ |
| CMI | ✅ |
| AnimatedScoreboard | ✅ |
| DeluxeMenus | ✅ |
| DeluxeChat | ✅ |
| HolographicDisplays | ✅ (via puente PAPI) |
| EssentialsX (chat format) | ✅ |

---

## 🛠️ Combinando con Otros Placeholders de PAPI

El enfriamiento es más útil cuando se combina con placeholders de jugador y servidor:

```yaml
# Una línea de estado completa para un HUD de supervivencia:
- "&7%player_name% &8| &7Saldo: &a$%vault_eco_balance%"
- "&7RTP &7▸ &f%safertp_cooldown%s"
```

> 💡 **Consejo:** ¿Quieres contar *cuántos* jugadores están actualmente en enfriamiento en todo el servidor? Combina `%safertp_can_use%` con extensiones de `Player_list` en tu plugin de marcadores.

---

🏠 [Inicio](Home) · 📦 [Instalación](Installation) · 🎮 [Comandos y Permisos](Commands-and-Permissions) · ⚙️ [Configuración](Configuration)
