> 🌐 [English](../Home.md) · **Español**

# 🌍 Wiki de SafeRTP

```
   ███████╗ █████╗ ███████╗███████╗██████╗ ████████╗██████╗
   ██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗╚══██╔══╝██╔══██╗
   ███████╗███████║█████╗  █████╗  ██████╔╝   ██║   ██████╔╝
   ╚════██║██╔══██║██╔══╝  ██╔══╝  ██╔══██╗   ██║   ██╔═══╝
   ███████║██║  ██║██║     ███████╗██║  ██║   ██║   ██║
   ╚══════╝╚═╝  ╚═╝╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝
```

**Teletransporte aleatorio seguro para Paper 1.21.x (probado en 1.21.10)** — carga de chunks asíncrona, comprobaciones de seguridad en múltiples pasadas, sin más muertes por lava.

> 🏛️ Parte de la suite de plugins de **TTS-Studio** — herramientas premium del lado del servidor, fabricadas para producción.

---

## ✨ Características Principales

| Característica | Descripción |
|----------------|-------------|
| 🎯 **Distribución por Anillo** | Puntos aleatorios uniformes entre `min-radius` y `max-radius` alrededor de un centro configurable |
| 🛡️ **Comprobaciones de Seguridad Multicapa** | Valida el suelo, espacio sobre la cabeza, lava/fuego/cactus/vacío, límites de altura del mundo y filtros de bioma |
| ⚙️ **Configuración por Mundo** | Radio, centro, lista blanca/negra de biomas, intentos máximos y coste — todo configurable por mundo |
| ⏱️ **Preparación Cancelable** | Cuenta atrás configurable mostrada mediante la API de Título; se cancela al moverse o recibir daño |
| 🕒 **Enfriamientos por Permiso** | Enfriamiento por defecto más anulaciones por rango mediante permisos `safertp.cooldown.<seconds>` |
| 💰 **Economía Vault** | Coste opcional por RTP, deducido solo en teletransporte exitoso — omisión por permiso |
| 📊 **PlaceholderAPI** | Enfriamiento restante en vivo y placeholders listos para usar |
| 🚀 **Carga Asíncrona de Chunks** | Usa PaperLib para cargar chunks candidatos fuera del hilo principal — sin picos de TPS |
| 🎨 **Marca TTS-Studio** | Banner de inicio enmarcado y prefijo de chat uniforme en toda la suite |

---

## 🎭 Experiencia en el Servidor

SafeRTP luce el estilo de la marca TTS-Studio — una firma que reconocerás en cada plugin de la suite.

### 🟣 Banner de Arranque en Consola

Al habilitarse, SafeRTP imprime un banner enmarcado en el color morado de TTS-Studio (`#C879FF`), seguido de la línea del plugin y un informe de estado en vivo:

```
╔══════════════════════════════════════╗
║        T T S - S T U D I O           ║
╚══════════════════════════════════════╝
   SafeRTP v1.0.0
   1 world(s) · Vault ✓ · PAPI ✓ · ready in 87ms
```

La línea de estado informa — de un vistazo — cuántos mundos tienen RTP habilitado, si Vault y PlaceholderAPI se han conectado correctamente, y cuánto tardó la activación.

### 💬 Prefijo de Chat

Cada mensaje que SafeRTP envía a un jugador lleva el prefijo unificado de la suite:

```
◈ SafeRTP: ¡Teletransportado a world (1234, 72, -5678)!
```

| Token | Color | Significado |
|-------|-------|-------------|
| `◈` | `gray` | Glifo de TTS-Studio — compartido en todos los plugins de la suite |
| `SafeRTP` | teal `#1ABC9C` | Nombre del plugin en su color de acento (7 chars, nombre completo) |
| `:` | `dark_gray` | Separador |

> 📝 **Nota:** SafeRTP **no** incluye un `ConfirmGui`. El flujo de `/rtp` tiene una ventana de preparación integrada durante la cual el jugador puede cancelar moviéndose — esa *es* la confirmación. No se necesita un aviso de GUI adicional.

---

## ⚡ Inicio Rápido

```bash
# 1. Coloca el jar en tu carpeta de plugins
cp safertp-1.0.0.jar plugins/

# 2. Inicia (o reinicia) el servidor — config.yml, worlds.yml y messages.yml
#    se generan automáticamente.

# 3. (Opcional) Instala Vault si quieres cobrar por cada RTP

# 4. Edita plugins/SafeRTP/worlds.yml — ajusta el radio y la lista negra de biomas

# 5. Aplica cambios sin reiniciar:
/rtp reload
```

> 💡 **Consejo:** Por defecto se teletransporta entre 1.000 y 10.000 bloques desde el spawn en el mundo principal, con los biomas de océano en lista negra — adecuado para la mayoría de servidores de supervivencia.

---

## 🧭 Navegación del Wiki

| Página | Contenido |
|--------|-----------|
| 📦 [Instalación](Installation) | Requisitos, despliegue del JAR, lista de verificación inicial |
| ⚙️ [Configuración](Configuration) | Referencia completa de `config.yml`, `worlds.yml` y `messages.yml` |
| 🎮 [Comandos y Permisos](Commands-and-Permissions) | Todos los comandos, tabla de permisos, niveles de enfriamiento |
| 📊 [PlaceholderAPI](PlaceholderAPI) | Todos los placeholders para marcadores, TAB y formato de chat |

---

## 📋 Requisitos

| Requisito | Versión | Notas |
|-----------|---------|-------|
| Paper | 1.21.x (probado en 1.21.10) | Spigot **no** está soportado |
| Java | 21+ | Requerido |
| Vault | latest + un plugin de economía | Opcional — habilita el `cost` por RTP |
| PlaceholderAPI | 2.11.6+ | Opcional — habilita los placeholders |

---

*SafeRTP — parte de la suite de plugins de **TTS-Studio** · teletransportes seguros al mundo abierto desde 2024 · [BuiltByBit](https://builtbybit.com)*
