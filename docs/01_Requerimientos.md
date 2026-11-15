# Documento de requerimientos: Bitácora de Lugares

**Proyecto final de Programación de Dispositivos Móviles (ISW211), UPAEP**
**Integrantes:** Emilio Perea Montes y Benito Abi Ramírez Vázquez

Bitácora de Lugares es una aplicación para Android que guarda cada lugar que visitas como una "parada": una foto tomada en el momento, la ubicación exacta obtenida con el GPS, una categoría, una nota y la fecha. Las paradas se guardan en el teléfono y se pueden buscar, filtrar, editar y abrir en el mapa para volver al lugar.

## 1. Problema o necesidad

Cuando visitamos un lugar que vale la pena recordar (un puesto de comida, un mirador, un buen lugar para estudiar), solemos tomar una foto. Con el tiempo esa foto queda perdida en la galería entre cientos de imágenes, y ya no sabemos dónde estaba el lugar, cómo llegar ni por qué lo queríamos recordar. Si además escribimos una nota, queda en otra aplicación, separada de la foto y sin la ubicación.

No hay un solo lugar que junte **qué es**, **cómo se ve**, **dónde está** y **por qué importa**, y que además permita encontrarlo después y volver a él.

## 2. Objetivo general

Desarrollar una aplicación móvil para Android que permita registrar, organizar y consultar lugares visitados, uniendo en un solo registro una foto tomada con la cámara, la ubicación obtenida con el GPS, una categoría y una nota, guardados de forma persistente en el teléfono para buscarlos, filtrarlos y abrirlos en el mapa.

## 3. Usuarios

| Usuario | Para qué la usa |
| --- | --- |
| Personas que viajan o salen de paseo | Guardar los lugares de un viaje o recorrido con su foto y ubicación |
| Estudiantes | Recordar lugares para estudiar o hacer tareas (bibliotecas, cafés con wifi) |
| Cualquier persona que explora su ciudad | Llevar una lista de lugares de comida, paisajes y sitios para volver |

Los usuarios solo necesitan saber usar un teléfono inteligente. La aplicación no requiere crear una cuenta ni conexión a internet para guardar y consultar paradas.

## 4. Funcionalidades principales

1. **Registrar una parada** con foto de la cámara, ubicación del GPS, título, categoría y nota.
2. **Ver la lista de paradas** con el título y la nota de cada una.
3. **Buscar y filtrar** por título o nota y por categoría, y ordenar por fecha o por nombre.
4. **Ver el detalle** de una parada con la foto, la categoría con su color, la fecha, las coordenadas y la nota.
5. **Abrir la ubicación en el mapa** para ver dónde está el lugar o cómo llegar.
6. **Editar y borrar** paradas, incluyendo cambiar la foto o volver a detectar la ubicación.

## 5. Requerimientos funcionales

| ID | Requerimiento |
| --- | --- |
| RF-01 | Al abrir la aplicación se muestra una pantalla de bienvenida con el logo de Aditus Studios, música de introducción y una barra de progreso, y después se abre la lista de paradas. |
| RF-02 | El usuario puede registrar una parada nueva con un título (obligatorio), una categoría (Comida, Paisaje, Escuela u Otro) y una nota opcional. |
| RF-03 | Desde el formulario, la aplicación abre la app de cámara del teléfono; el usuario toma la foto y la aplicación la recibe, la muestra en el formulario y permite reemplazarla con otra. |
| RF-04 | Al crear una parada, la aplicación obtiene automáticamente la ubicación con el GPS y muestra las coordenadas y su precisión en metros. El usuario puede volver a detectar la ubicación. |
| RF-05 | Antes de guardar, la aplicación valida que la parada tenga título, foto y ubicación, y muestra un mensaje que indica qué falta. |
| RF-06 | La aplicación guarda cada parada en una base de datos local junto con la fecha y hora de registro. |
| RF-07 | La pantalla principal muestra la lista de paradas con el título y la nota de cada una, y un contador de paradas. |
| RF-08 | El usuario puede buscar paradas por título o nota; la lista se filtra mientras escribe. |
| RF-09 | El usuario puede filtrar la lista por categoría y combinar el filtro con la búsqueda. |
| RF-10 | El usuario puede ordenar la lista por más recientes o por nombre desde el menú de opciones, y la aplicación recuerda el orden elegido. |
| RF-11 | Al tocar una parada se muestra su detalle: foto, categoría con su color, título, fecha y hora, coordenadas y nota. |
| RF-12 | Desde el detalle, el usuario puede abrir la ubicación de la parada en una aplicación de mapas, con un marcador con el título. |
| RF-13 | El usuario puede editar una parada guardada: título, categoría, nota, foto y ubicación. |
| RF-14 | El usuario puede borrar una parada después de confirmar en un diálogo; también se borra su foto. |
| RF-15 | Al mantener presionada una parada en la lista se muestra un menú contextual con las opciones Ver detalle, Editar y Borrar. |
| RF-16 | El menú de opciones incluye "Acerca de", con la descripción de la aplicación y los nombres de los integrantes. |
| RF-17 | La lista muestra un mensaje cuando todavía no hay paradas y otro cuando la búsqueda o el filtro no encuentran resultados. |
| RF-18 | La aplicación pide el permiso de ubicación y avisa si el usuario lo niega, si el GPS está apagado o si no hay una aplicación de cámara o de mapas disponible. |

## 6. Requerimientos no funcionales

| ID | Tipo | Requerimiento |
| --- | --- | --- |
| RNF-01 | Plataforma | La aplicación se desarrolla en Java con Android Studio y se instala y ejecuta en un teléfono Android real. |
| RNF-02 | Compatibilidad | La interfaz se ve correctamente tanto en versiones anteriores de Android como en Android 15 y 16, donde el contenido se dibuja detrás de las barras del sistema. |
| RNF-03 | Persistencia | Las paradas, fotos y preferencias se conservan al cerrar la aplicación o reiniciar el teléfono, y funcionan sin conexión a internet. |
| RNF-04 | Privacidad | Los datos y las fotos se guardan en la carpeta privada de la aplicación. Solo se pide el permiso de ubicación mientras la app está en uso; no se usan cuentas ni servidores. |
| RNF-05 | Rendimiento | La foto que regresa la cámara se guarda como un archivo pequeño en la carpeta privada de la app y la base de datos solo guarda su ruta, para que la base de datos se mantenga ligera. |
| RNF-06 | Batería | El GPS deja de buscar cuando la precisión es de 15 m o menos, o cuando la pantalla del formulario deja de estar visible. |
| RNF-07 | Usabilidad | Los campos tienen textos de ayuda, los botones principales miden al menos 48 dp de alto, cada categoría tiene su color y las acciones que no se pueden deshacer piden confirmación. |
| RNF-08 | Idiomas | Todos los textos de la interfaz están en `strings.xml`, con traducciones a español de México, inglés y francés. |
| RNF-09 | Integridad de datos | Las consultas a la base de datos pasan los valores como argumentos (`?`), y la categoría se guarda con un nombre fijo que no depende del idioma del teléfono. |
| RNF-10 | Mantenibilidad | El código separa el modelo (`Parada`), el acceso a datos (`DatabaseHelper`) y las utilidades de fotos (`UtilFotos`); colores, medidas y estilos están en `values`. |

## 7. Relación con la documentación del proyecto

- **Prototipos:** `docs/02_Prototipos/` muestra las pantallas principales y la navegación entre ellas.
- **Modelo de base de datos:** `docs/03_Base_de_datos/` contiene el script, el diagrama y la explicación de cómo se guarda, consulta y actualiza la información.
- **Código fuente:** el proyecto de Android Studio está en la carpeta `app/` del repositorio.
