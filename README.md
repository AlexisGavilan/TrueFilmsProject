# 📸 Plan de Organización Agile y Especificaciones del Proyecto

Este documento recoge la planificación, distribución en **Sprints**, requisitos de software y estimación de costes para el desarrollo de la plataforma web del fotógrafo.

---

## 🎯 Resumen Ejecutivo

* **Objetivo:** Crear una plataforma web personalizada para un fotógrafo que permita exponer su trabajo, publicar cortometrajes/documentales y compartir galerías privadas de eventos con descarga personalizada para sus clientes.
* **Metodología:** Agile / Scrum por Sprints.
* **Stack Tecnológico:**
    * **Backend:** Java 21 + Spring Boot 3 + PostgreSQL.
    * **Frontend:** Next.js (TypeScript + Tailwind CSS).
    * **Infraestructura:** Servidor Único VPS (Hetzner / OVH) con almacenamiento local en disco.

---

## 🚀 Planificación de Sprints (Metodología Agile)

### 📌 Sprint 1: Estructura Base, Navegación y Secciones Públicas
**Objetivo:** Disponer de la web funcional con la navegación de 3 pestañas principales y la presentación pública del fotógrafo.

* **HU-1.1 (Navegación Modular):** Como visitante, quiero un menú de navegación superior accesible con 3 pestañas principales (y preparación modular para añadir una 4ª pestaña en el futuro).
* **HU-1.2 (Pestaña 1 - Presentación y Portfolio):** Como visitante, quiero ver la biografía/presentación del fotógrafo y una galería con una selección de sus mejores fotos y vídeos.
* **HU-1.3 (Pestaña 3 - Cortometrajes/Documentales):** Como visitante, quiero acceder a un espacio donde reproducir los vídeos tipo documental y cortometrajes publicados.
<!--* ⏱️ **Estimación:** 14 – 18 horas.-->

---

### 📌 Sprint 2: Gestión de Eventos y Sistema de Seguridad/Acceso
**Objetivo:** Implementar la lógica de protección por código de privacidad diario/evento para la zona privada.

* **HU-2.1 (Pestaña 2 - Control de Acceso por Código):** Como cliente del evento, quiero ingresar un código de acceso para acceder de forma privada a la galería de mi evento.
* **HU-2.2 (Panel Admin - Gestión de Códigos):** Como fotógrafo, quiero gestionar y actualizar el código de acceso (diario o por evento) para controlar la privacidad de las fotos.
<!--* ⏱️ **Estimación:** 10 – 14 horas.-->

---

### 📌 Sprint 3: Galería Interactiva, Previsualización y Descarga Personalizada
**Objetivo:** Permitir la visualización individual con miniaturas y la descarga del paquete comprimido de fotos seleccionadas.

* **HU-3.1 (Previsualización de Fotos):** Como cliente, quiero previsualizar la galería (~200 fotos por evento en `.jpg`/`.png`) mediante miniaturas (*thumbnails*) optimizadas para carga rápida.
* **HU-3.2 (Selección de Fotografías):** Como cliente, quiero marcar/seleccionar las fotos que deseo descargar.
* **HU-3.3 (Descarga Personalizada):** Como cliente, quiero descargar un archivo comprimido `.zip` únicamente con el conjunto de fotos que he seleccionado.
<!--* ⏱️ **Estimación:** 28 – 36 horas.-->

---

### 📌 Sprint 4: Ciclo de Vida de Archivos y Panel Admin
**Objetivo:** Optimizar el espacio del servidor mediante la eliminación automática de eventos caducados.

* **HU-4.1 (Caducidad de Archivos):** Como sistema, las fotografías de un evento deben permanecer disponibles un máximo de **5 días**.
* **HU-4.2 (Borrado Automático - `@Scheduled`):** Como fotógrafo, quiero que el servidor elimine automáticamente los archivos de los eventos pasados los 5 días para liberar espacio en disco.
* **HU-4.3 (Panel de Gestión del Fotógrafo):** Formulario para crear eventos, subir paquetes de fotos y visualizar el espacio disponible en disco.
<!--* ⏱️ **Estimación:** 14 – 20 horas.-->

---

### 📌 Sprint 5: Testing, Despliegue en VPS y Dominio
**Objetivo:** Desplegar la aplicación en producción en un servidor VPS con dominio propio.

* Pruebas unitarias e integración (JUnit 5 / Mockito).
* Configuración del VPS (Ubuntu, Docker, PostgreSQL, Nginx, SSL con Let's Encrypt).
* Configuración e integración del dominio personalizado (`.com` / `.es`).
<!--* ⏱️ **Estimación:** 12 – 16 horas.-->

---

## 🌐 Costes Recurrentes de Infraestructura (Opción A: Servidor VPS)

| Concepto | Proveedor / Especificación | Coste Estimado |
| :--- | :--- | :--- |
| **Dominio** | `.com` / `.es` (Namecheap, OVH, etc.) | ~12 € / año |
| **Servidor VPS** | Hetzner / OVH (160 GB SSD / 4GB RAM) | ~15 € / mes (~180 € / año) |
| **TOTAL MANTENIMIENTO** | **Infraestructura + Dominio** | **~192 € / año** |