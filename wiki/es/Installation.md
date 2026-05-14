> 🌐 [English](../Installation.md) · **Español**

# 📦 Instalación

Esta página te guía para poner en marcha SafeRTP — parte de la suite de plugins de **TTS-Studio** — en tu servidor Paper desde cero hasta operativo.

---

## 🔧 Requisitos

| Software | Versión Mínima | ¿Requerido? |
|----------|----------------|-------------|
| [Paper](https://papermc.io/) | **1.21.x** (probado en 1.21.10) | ✅ Sí |
| Java | **21** | ✅ Sí |
| [Vault](https://www.spigotmc.org/resources/vault.34315/) + plugin de economía | latest | ⚡ Recomendado |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | 2.11.6+ | ⚡ Opcional |

> ⚠️ **Advertencia:** SafeRTP usa APIs específicas de Paper (`PaperLib` para chunks asíncronos, títulos de Adventure). Spigot, Purpur sin core de Paper o Folia **no** están soportados (`folia-supported: false`).

> ⚠️ **Advertencia:** Java 21 es el mínimo. Ejecutar en Java 17 causará que el plugin no cargue.

---

## 🚀 Instalación Paso a Paso

### Paso 1 — Descarga el JAR

Compra y descarga `safertp-1.0.0.jar` desde BuiltByBit. Usa siempre la descarga oficial — no uses versiones crackeadas.

### Paso 2 — Despliega el JAR

```bash
# Copia el JAR en tu carpeta de plugins
cp safertp-1.0.0.jar /tu/servidor/plugins/

# Tu directorio de plugins debería contener ahora:
# plugins/
# ├── SafeRTP-1.0.0.jar
# ├── Vault.jar          (recomendado)
# └── ...otros plugins...
```

### Paso 3 — Primer Arranque

Inicia o reinicia tu servidor. SafeRTP genera su configuración automáticamente e imprime el banner de arranque de TTS-Studio:

```
╔══════════════════════════════════════╗
║        T T S - S T U D I O           ║
╚══════════════════════════════════════╝
   SafeRTP v1.0.0
   1 world(s) · Vault ✓ · PAPI ✓ · ready in 87ms
```

```
plugins/SafeRTP/
├── config.yml        ← configuración principal (actualmente mínima)
├── worlds.yml        ← radio por mundo, filtros de bioma, coste
└── messages.yml      ← cadenas dirigidas al jugador (MiniMessage)
```

### Paso 4 — (Opcional) Instala Vault

Si quieres cobrar a los jugadores por cada `/rtp`, instala Vault más cualquier plugin de economía compatible (EssentialsX, CMI, etc.):

```bash
# Coloca Vault.jar y tu plugin de economía en plugins/
# Reinicia el servidor
# SafeRTP detectará Vault automáticamente al próximo enable:
[SafeRTP] Vault economy hooked.
```

Luego establece `cost: <cantidad>` por mundo en `worlds.yml` — consulta [Configuración](Configuration).

### Paso 5 — (Opcional) Instala PlaceholderAPI

Si quieres placeholders de enfriamiento en marcadores/TAB/chat:

```bash
# Coloca PlaceholderAPI.jar en plugins/
# Reinicia. SafeRTP registra su expansión automáticamente:
[SafeRTP] PlaceholderAPI expansion registered.
```

No se necesita descarga de `ecloud` — la expansión viene **dentro** del jar de SafeRTP.

### Paso 6 — Ajusta y Recarga

Edita `worlds.yml` para establecer radios sensatos y una lista negra de biomas para *tu* mundo (consulta [Configuración](Configuration)), luego:

```
/rtp reload
```

No se necesita reiniciar el servidor para cambios de configuración.

---

## ✅ Lista de Verificación Post-Instalación

- [ ] El JAR está en la carpeta `plugins/`
- [ ] El servidor se reinició al menos una vez para generar las configuraciones
- [ ] El banner de arranque de TTS-Studio es visible en consola
- [ ] `worlds.yml` revisado — `min-radius` / `max-radius` tienen sentido para tu mapa
- [ ] Vault detectado si se establece `cost` *(opcional)*
- [ ] PlaceholderAPI detectado si usas marcadores *(opcional)*
- [ ] Grupos de permisos configurados en tu plugin de permisos
- [ ] Comando de prueba ejecutado: `/rtp` como jugador que no es OP

---

## 🔄 Actualizar SafeRTP

1. Detén el servidor
2. Reemplaza el JAR antiguo con el nuevo (los archivos de configuración se conservan en disco)
3. Inicia el servidor
4. Ejecuta `/rtp reload` si se te indica para releer las configuraciones

> 💡 **Consejo:** Haz una copia de seguridad de `plugins/SafeRTP/` antes de actualizar, especialmente si has personalizado `messages.yml` para traducción.

---

## 🗑️ Desinstalación

1. Elimina `SafeRTP-*.jar` de `plugins/`
2. Opcionalmente elimina `plugins/SafeRTP/` para borrar todas las configuraciones

SafeRTP no almacena ninguna base de datos — todos los enfriamientos están solo en memoria y se borran al apagar.

---

## 🆘 Solución de Problemas

| Síntoma | Causa Probable | Solución |
|---------|----------------|----------|
| El plugin no carga | Java < 21 | Actualiza el JRE a Java 21+ |
| `Folia is not supported` | Ejecutando en Folia | Usa Paper 1.21.x — el soporte de Folia está en la hoja de ruta |
| Sin banner de TTS-Studio al habilitar | Versión de Paper demasiado antigua | Usa Paper 1.21.x (probado en 1.21.10) |
| Falta `Vault economy hooked.` | Vault no instalado o sin proveedor de economía | Instala Vault + un plugin de economía (EssentialsX/CMI) |
| Los jugadores siguen aterrizando en océanos | Lista negra de biomas no aplicada | Confirma `biome-blacklist` en `worlds.yml`, luego `/rtp reload` |
| `No se encontró una ubicación segura` repetidamente | `max-attempts` demasiado bajo, o el radio es mayormente agua/lava | Aumenta `max-attempts`, amplía la lista negra o mueve las coordenadas del centro |
| El enfriamiento no se respeta | El jugador tiene `safertp.bypass.cooldown` | Revoca el permiso para jugadores que no sean staff |
| Los placeholders muestran `%safertp_...%` | PlaceholderAPI no instalado | Instala PAPI; la expansión está integrada |

---

🏠 [Inicio](Home) · ⚙️ [Configuración](Configuration) · 🎮 [Comandos y Permisos](Commands-and-Permissions)
