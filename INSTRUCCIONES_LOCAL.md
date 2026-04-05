# 🦷 OdontoApp - Guía de Configuración Local

Bienvenido al repositorio de OdontoApp. Sigue estos pasos para levantar el proyecto en tu entorno local sin problemas de contraseñas o servicios faltantes.

## 1. Configuración de Variables de Entorno (.env)

Por razones de seguridad, las contraseñas de la base de datos y del servidor de correos no están en el código fuente.

1. En la raíz del proyecto (donde está este archivo), copia el archivo `.env.example`.
2. Renombra la copia para que se llame **exactamente** `.env` (con el punto inicial).
3. Abre tu nuevo archivo `.env` y llena los datos correspondientes:

```env
DB_PASSWORD=tu_contraseña_local_de_mysql
SMTP_USERNAME=tu_correo_de_pruebas@gmail.com
SMTP_PASSWORD=tu_clave_de_aplicacion_gmail
```
*(Si usas Gmail, recuerda que debes generar una "Contraseña de aplicación" en los ajustes de seguridad de Google, no uses tu contraseña normal).*

## 2. Levantar el Proyecto con Docker

El proyecto está dockerizado para que no tengas que instalar MySQL manualmente ni lidiar con versiones de Java. 

Abre tu terminal en la carpeta principal del proyecto y ejecuta:

```bash
docker compose up --build
```

Esto descargará todo, creará la base de datos `odontoapp_db` y levantará el backend en el puerto `8080`.

## 3. Datos de Prueba (Seed Data)

El sistema genera automáticamente datos de prueba iniciales cuando arranca. Puedes probar la aplicación usando cualquiera de las siguientes cuentas:

| Rol | Correo | Contraseña |
| --- | --- | --- |
| 👑 Administrador | `admin@odontoapp.com` | `admin123` |
| 👨‍⚕️ Odontólogo | `odontologo@odontoapp.com` | `odonto123` |
| 🙋‍♀️ Recepcionista| `recepcion@odontoapp.com` | `recep123` |
| 📦 Almacén | `almacen@odontoapp.com` | `almacen123` |
| 🦷 Paciente | `paciente@odontoapp.com` | `paciente123` |

## Notas adicionales

- **Base de Datos:** Docker expone el puerto `3307` hacia tu computadora. Si quieres conectar un administrador visual como DBeaver o DataGrip, usa `localhost:3307` y las credenciales que pusiste en tu `.env`.
- **Nuevos Cambios:** Siempre que edites código Java, recuerda detener el contenedor y volver a ejecutar `docker compose up --build`.

¡Feliz código! 🚀
