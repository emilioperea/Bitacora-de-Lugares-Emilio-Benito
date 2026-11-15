-- =====================================================================
-- Bitácora de Lugares - Modelo de base de datos (SQLite)
-- Proyecto final - Programación de Dispositivos Móviles (ISW211)
-- Integrantes: Emilio Perea Montes y Benito Abi Ramírez Vázquez
--
-- Base de datos: bitacora (versión 1)
-- La crea la clase DatabaseHelper (hereda de SQLiteOpenHelper) en su
-- método onCreate() la primera vez que se abre la aplicación.
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. Creación de la tabla (igual que en DatabaseHelper.onCreate)
-- ---------------------------------------------------------------------
create table paradas (
    _id       integer primary key autoincrement,  -- Identificador único de la parada
    titulo    text not null,                      -- Nombre de la parada
    categoria text not null,                      -- Comida, Paisaje, Escuela u Otro
    nota      text,                               -- Texto libre (puede estar vacío)
    foto      text,                               -- Ruta del archivo .jpg de la foto
    latitud   real,                               -- Latitud obtenida del GPS
    longitud  real,                               -- Longitud obtenida del GPS
    fecha     integer                             -- Fecha y hora de registro en milisegundos desde 1970
);


-- ---------------------------------------------------------------------
-- 2. Paradas de ejemplo que se insertan al crear la base de datos
--    (las fechas reales se calculan con System.currentTimeMillis())
-- ---------------------------------------------------------------------
insert into paradas (titulo, categoria, nota, foto, latitud, longitud, fecha)
values ('Atardecer en Cholula', 'Paisaje',
        'Subir a la pirámide como a las 6 pm. Con cielo despejado se ve el volcán.',
        '', 19.05741, -98.30219, 1793146200000);

insert into paradas (titulo, categoria, nota, foto, latitud, longitud, fecha)
values ('El Patio y las Ranas', 'Comida',
        'Taquería en la Av. 2 Poniente, en el centro. Más conocida como Las Ranas. Pedir los alambres.',
        '', 19.04537, -98.19876, 1793737500000);

insert into paradas (titulo, categoria, nota, foto, latitud, longitud, fecha)
values ('Edificio de posgrados de la UPAEP', 'Escuela',
        'Sobre la calle 17 Sur, cerca del campus central de la UPAEP.',
        '', 19.04690, -98.21208, 1794069600000);


-- ---------------------------------------------------------------------
-- 3. Consultas que hace la aplicación
--    En el código los valores van como "?" y se pasan aparte
--    (argumentos de query, update y delete).
-- ---------------------------------------------------------------------

-- 3.1 Lista de paradas (rawQuery de MainActivity, la muestra un SimpleCursorAdapter)
--     Búsqueda por título o nota, más recientes primero
SELECT _id, titulo, nota FROM paradas
WHERE (titulo LIKE '%cholula%' OR nota LIKE '%cholula%')
ORDER BY fecha DESC;

--     Búsqueda con filtro de categoría, ordenada por nombre
SELECT _id, titulo, nota FROM paradas
WHERE (titulo LIKE '%%' OR nota LIKE '%%') AND categoria = 'Comida'
ORDER BY titulo;

-- 3.2 Una sola parada (DatabaseHelper.obtenParada)
select * from paradas where _id = 1;

-- 3.3 Guardar una parada nueva (insert de FormularioActivity)
insert into paradas (titulo, categoria, nota, foto, latitud, longitud, fecha)
values ('Café para trabajar', 'Otro', 'Wifi rápido y enchufes en la barra.',
        '/data/user/0/com.aditusstudios.bitacoralugares/files/parada_1794070000000.jpg',
        19.04120, -98.20631, 1794070000000);

-- 3.4 Editar una parada (DatabaseHelper.actualizaParada)
update paradas
set titulo = 'Café para estudiar', categoria = 'Escuela', nota = 'Cierra a las 9 pm.',
    latitud = 19.04125, longitud = -98.20640
where _id = 4;

-- 3.5 Borrar una parada (DatabaseHelper.borraParada)
--     La aplicación también borra el archivo de la foto con UtilFotos.borraFoto()
delete from paradas where _id = 4;
