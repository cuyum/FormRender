Autor: [Cuyum](http://www.cuyum.com/) 
        
Fecha:	30 de Agosto, 2013

#FormRender

Aplicaci&oacute;n web que permite on the fly leer un formulario definido en formato XML (xform) y transformarlo/renderizarlo en un HTML. Incluye la funcionalidad de upload de Formularios (XML) y tambi&eacute;n de archivos formato XSL que aplicar&aacute; al  realizar la transformaci&oacute;n.

<span id="0"/></span>

#&Iacute;ndice

1. [Objetivos del documento](#1)
2. [Introducci&oacute;n](#2)
3. [Tecnolog&iacute;a](#3)
4. [Arquitectura](#4)
	* [Vista L&oacute;gica](#41)
		- [Capa Web](#411)
		- [Capa Servicios](#412)
		- [Capa Procesador XSLT](#413)
		- [Capa Seguridad ](#414)
		- [Capa Transaccional](#415)
		- [Capa Integraci&oacute;n](#416)
	* [Vista F&iacute;sica](#42)
		- [Nodos Clientes](#421)
		- [Nodos Servidor](#422)
		- [Nodos Data](#423)
5. [Compilaci&oacute;n, Instalaci&oacute;n y Ejecuci&oacute;n](#5)
	- [Listado de componentes necesarios](#51)
	- [Requisitos m&iacute;nimos](#52)
	- [Configuraci&oacute;n](#53)
	- [Descarga, Compilaci&oacute;n y Ejecuci&oacute;n](#54)	
6. [Uso FormRender](#6)
	- [Subida de archivos XML](#61)
	- [Subida de archivos XSL](#62)
	- [Acceso / Llamada al servicio](#63)
7. [Mantenimiento FormRender](#7)
	- [Servicios](#71)


----------
<span id="1"/></span>
##1. Objetivo del documento <span style="font-size:8px;">([Arriba](#0))</span>


Presentar una visi&oacute;n general de las caracter&iacute;sticas de la aplicaci&oacute;n **FormRender** y sus aspectos t&eacute;cnicos de mayor relevancia, especificando las caracter&iacute;sticas t&eacute;cnicas y de arquitectura que tiene la plataforma, entre ellos los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicaci&oacute;n.


<span id="2"/></span>
##2. Introducci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>

La funci&oacute;n principal de FormRender es poder generar formularios HTML a partir de una especificaci&oacute;n basada en el estandar [xForms](http://www.w3.org/MarkUp/Forms/). 
Los documentos xForms son documentos XML los cuales a trav&eacute;s de una pantalla administrativa se cargan un con un identificador para luego ser recuperados e integrados a otras aplicaciones. FormRender internamente utiliza un procesador XSL para a trav&eacute;s de un XSLT (Procesador de Transformaci&oacute;n XSL) generar el c&oacute;digo HTML correspondiente con sus correspondientes estilos CSS y funcionalidad JavaScript.
<center>
<img src="http://s21.postimg.org/4z4bvlgvr/funcionalidad.png" />
</center>
----------

<span id="3"/></span>
##3. Tecnolog&iacute;a <span style="font-size:8px;">([Arriba](#0))</span>

Es un sistema transaccional cuya interface a usuario es facilitada mediante el uso de tecnolog&iacute;a web compatible con las &uacute;ltimas versiones de los Browsers de Internet (Internet Explorer, Firefox, Chrome).
El uso de esta tecnolog&iacute;a permitir&aacute; al sistema ser accesible desde cualquier lugar que cuente con conexi&oacute;n a Internet, podr&aacute; ser instalado y desplegado en servidores propios, externos o en “Clouds” lo que facilita la escalabilidad de la aplicaci&oacute;n en caso de ser necesaria mayor carga de trabajo. 

El sistema est&aacute; construido utilizando plataforma y est&aacute;ndares de desarrollo **JEE 6** (Java Enterprise Edition). Como implementaci&oacute;n de este est&aacute;ndar se utiliza el stack tecnol&oacute;gico porvisto por **JBoss 7.1.0** [https://www.jboss.org/jbossas/](https://www.jboss.org/jbossas/) cuyas implementaciones principales son las siguientes:

<center>
<table>
<thead>
<tr>
<th>Technolog&iacute;a/Especificaci&oacute;n</th>
<th>JBoss 7.1</th>
</tr>
</thead>
<tbody>
<tr>
<td>Java EE[JSR-151,244,316]</td>
<td style="text-align:right;">6.0</td>
</tr>
<tr>
<td>Java Servlet [JSR-154, 315]</td>
<td style="text-align:right;">3.0</td>
</tr>
<tr>
<td>JavaServer Faces (JSF) [JSR-252, 314]</td>
<td style="text-align:right;">2.0</td>
</tr>
<tr>
<td>JavaServer Pages & Expression Language (JSP) [JSR-245]</td>
<td style="text-align:right;">2.2</td>
</tr>
<tr>
<td>Java Transaction API (JTA) [JSR-907]</td>
<td style="text-align:right;">1.1</td>
</tr>
<tr>
<td>Enterprise JavaBeans with Interceptors 1.1 (EJB) [JSR-153, 220, 318]</td>
<td style="text-align:right;">3.1</td>
</tr>
<tr>
<td>Enterprise JavaBeans with Interceptors 1.1 (EJB) [JSR-153, 220, 318]</td>
<td style="text-align:right;">3.1</td>
</tr>
<tr>
<td>Java EE Connector Architecture [JSR-112, 322]</td>
<td style="text-align:right;">1.6</td>
</tr>
<tr>
<td>JavaMail [JSR-919 ]</td>
<td style="text-align:right;">1.4</td>
</tr>
<tr>
<td>Java Message Service (JMS) [JSR-914]</td>
<t style="text-align:right;"d>1.1</td>
</tr>
<tr>
<td>Java Persistence (JPA) [JSR-220, 317]</td>
<td style="text-align:right;">2.0</td>
</tr>
<tr>
<td>Java API for XML Web Services (JAX-WS) [JSR-224]</td>
<td style="text-align:right;">2.2</td>
</tr>
<tr>
<td>Common Annotations for the Java Platform [JSR-250]</td>
<td style="text-align:right;">1.1</td>
</tr>
<tr>
<td>Java API for RESTful Web Services (JAX-RS) [JSR-311]</td>
<td style="text-align:right;">1.1</td>
</tr>
<tr>
<td>Contexts and Dependency Injection for Java (CDI) [JSR-299]</td>
<td style="text-align:right;">1.0</td>
</tr>
<tr>
<td>Bean Validation [JSR-303]</td>
<td style="text-align:right;">1.0</td>
</tr>
</tbody>
</table>
</center>

Como tecnolog&iacute;a de soporte para el mantenimiento de la informaci&oacute;n se utiliza base de datos relacional [PostgreSQL](http://www.postgresql.org/)

----------------
<span id="4"/></span>

##4. Arquitectura <span style="font-size:8px;">([Arriba](#0))</span>

Se pueden detallar 2 puntos de vista generales, la vista L&oacute;gica, que representa el stack de tecnolog&iacute;as utilizadas para desarrollar la plataforma, y la vista f&iacute;sica, que representa la distribuci&oacute;n f&iacute;sica de los componentes

<span id="41"/></span>

###4.1. Vista L&oacute;gica <span style="font-size:8px;">([Arriba](#0))</span>

El siguiente diagrama representa una vista conceptual de la Arquitectura por capas de la aplicaci&oacute;n, donde se puede ver para cada una de ellas la tecnolog&iacute;a primaria utilizada para llevar a cabo la funcionalidad.
<center>
<table border="1">
<tr><td style="text-align:center;background-color:FF9933;" colspan="4">Web Servlet/JSF2</td></tr>
<tr><td style="text-align:center;background-color:66CCFF;" colspan="4">Servicios <br/>CDI/POJO</td></tr>
<tr>
<td style="text-align:center;background-color:66CC66;">JPA2 Transaccional</td>
<td style="text-align:center;background-color:66CC66;">Procesador XSLT</td>
<td style="text-align:center;background-color:66CC66;">Integraci&oacute;n <br/> JAX-WS/JAX-RS</td>
<td style="text-align:center;background-color:66CC66;">Seguridad Conector<br/> POJO</td>
</tr>
</table>
</center>

<span id="411"/></span>

####Capa Web <span style="font-size:8px;">([Arriba](#0))</span>
El objetivo de la capa web es proveer una interfaz de acceso al sistema para el usuario final. En esta capa se utiliza la implementaci&oacute;n de **JSF2** Primefaces para construir las interfaces a usuarios y **Servlet** para resolver la funci&oacute;n principal del sistema que es devolver un formulario especificado en HTML.

<span id="412"/></span>

####Capa Servicios <span style="font-size:8px;">([Arriba](#0))</span>
Esta capa brinda un nivel de abstracci&oacute;n para acceso a la l&oacute;gica de la aplicaci&oacute;n, proveyendo as&iacute; un conjunto de servicios uniformes y transparentes a los clientes mediante el uso **CDI**. Esta tecnolog&iacute;a permite ofrecer **POJOs** como servicios y brinda facilidades de integraci&oacute;n entre la capa de presentaci&oacute;n, los servicios de negocio y los m&oacute;dulos restantes.

<span id="413"/></span>

####Capa Procesador XSLT <span style="font-size:8px;">([Arriba](#0))</span>
Representa el coraz&oacute;n de la aplicaci&oacute;n, tiene la responsabilidad de transformar, leyendo de una base de formularios, la especificaci&oacute;n de un formulario xForm en un HTML con sus estilos CSS y librer&iacute;as JavaScript. Se est&aacute; utilizando Saxon como engine XSLT para realizar la transformaci&oacute;n.

<span id="414"/></span>

####Capa Seguridad <span style="font-size:8px;">([Arriba](#0))</span>
Esta capa brinda una interface para que la aplicaci&oacute;n pueda resolver sus necesidades de autorizaci&oacute;n y autenticaci&oacute;n. Existe una implementaci&oacute;n default que resuelve estas cuestiones accediendo a un repositorio Redis.

<span id="415"/></span>

####Capa Transaccional <span style="font-size:8px;">([Arriba](#0))</span>
Su objetivo es brindar de una manera homog&eacute;nea y transparente, mediante el uso del est&aacute;ndar de persistencia **JPA2**, el acceso a la informaci&oacute;n al resto de la aplicaci&oacute;n independiz&aacute;ndolo de la base de datos f&iacute;sica con la que interact&uacute;a.

<span id="416"/></span>

####Capa Integraci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>
Provee interfaces para acceder a datos externos a la aplicaci&oacute;n que necesiten los formularios. Las implementaciones de estas interfaces se realizan mediante WebServices SOAP (**JAX-WS**) o Rest Services (**JAX-RS**) 

<span id="42"/></span>

###4.2. Vista F&iacute;sica <span style="font-size:8px;">([Arriba](#0))</span>


<center>
<img src="http://s24.postimg.org/4dkvg3yut/fisica.png"/>
</center>

<span id="421"/></span>
####Nodos Clientes <span style="font-size:8px;">([Arriba](#0))</span>
Los navegadores de Internet  son el medio por el cual los usuarios interact&uacute;an con la aplicaci&oacute;n. Ejemplo de ellos son Internet Explorer, Firefox y Google Chrome, deben tener soporte para AJAX. Se comunican a trav&eacute;s del protocolo HTTP y renderizan las paginas HTML que visualizar&aacute; el usuario.

<span id="422"/></span>
####Nodos Servidor
Este nodo, o mejor dicho instancias de este nodo, ya que se podr&iacute;a tener m&aacute;s de un Application Server brindando servicios esta, compuesto por instancias de JBoss 7 el cual contiene un EJB Container y un Mojarra embebido (Servlet Container) el cual brindaran los servicios de middleware (protocolos de comunicaci&oacute;n entre componentes, control de transacciones locales y/o distribuidas, mecanismos de intercepci&oacute;n de eventos para control de seguridad y auditoria, manejos de excepciones y servicios de logging) al software a construir, el cual contiene diferentes m&oacute;dulos con responsabilidades bien definidas.
Este servidor contendr&aacute; un Redis (motor de base de datos de memoria) para mantener informaci&oacute;n de contexto que el modulo de Seguridad utilizar&aacute; para resolver sus servicios.

<span id="423"/></span>
####Nodos Data
Estos nodos contienen distintas fuentes de informaci&oacute;n con las que interactuar&aacute; el sistema. Podemos distinguir dos tipos:
Base de Datos: el sistema deber&aacute; interactuar con una base de datos PostgreSQL que servir&aacute; de informaci&oacute;n al modulo Transaccional, de Procesamiento y de Integraci&oacute;n.
Almacenamiento: utilizado por lo general para guardar im&aacute;genes, logs y archivos con informaci&oacute;n de operaciones.

----------------

<span id="5"/></span>

##5. Compilaci&oacute;n, Instalaci&oacute;n y Ejecuci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>

En &eacute;sta secci&oacute;n se detalla todo lo necesario para compilar, instalar o deployar y ejecutar la plataforma. Se asume que los siguientes componentes, necesarios para dichas tareas, se encuentran instalados y corriendo normalmente en el sistema operativo.

<span id="51"/></span>
####5.1 Listado de componentes necesarios para poder ejecutar la aplicaci&oacute;n:

- JDK 1.6.x ([Gu&iacute;a de instalaci&oacute;n](https://help.ubuntu.com/community/Java))
- Jboss-as-7.1.0.Final ([Descarga](http://www.jboss.org/jbossas/downloads/))
- Maven 3.0.4 ([Descarga](http://maven.apache.org/download.cgi),[Instalaci&oacute;n](http://maven.apache.org/download.cgi#Unix-based_Operating_Systems_Linux_Solaris_and_Mac_OS_X))
- PostgreSQL 9.1 ([Gu&iacute;a de instalaci&oacute;n](https://help.ubuntu.com/13.04/serverguide/postgresql.html))
- Git (Solo para entorno de desarrollo, [Gu&iacute;a de instalaci&oacute;n](https://help.ubuntu.com/community/Git))

<span id="52"/></span>
####5.2 Requisitos M&iacute;nimos:

Es necesario tener instalados (al menos) 2Gb de ram.

<span id="53"/></span>
####5.3 Instalaci&oacute;n y Configuraci&oacute;n de entorno <span style="font-size:8px;">([Arriba](#0))</span>

Agregar al archivo __<jboss-as-7.1.0.Final>/standalone/configuration/standalone.xml__

En la secci&oacute;n __<datasources>__ la siguiente entrada, especificando usuario y password correspondiente para habilitar el Data Source correctamente en Jboss:

		...
		<datasources>
		...	
			<datasource jta="true" jndi-name="java:jboss/datasources/FormRenderDS" pool-name="FormRenderDS" enabled="true" use-java-context="true" use-ccm="true">
	            		<connection-url>jdbc:postgresql://localhost:5432/formrender</connection-url>
          	  		<driver-class>org.postgresql.Driver</driver-class>
	            		<driver>postgresql</driver>
          	  		<security>
					<user-name>${username}</user-name>
          	      			<password>${password}</password>
	          		</security>
			</datasource>  
		...              
			<drivers>    
		...                
				<driver name="postgresql" module="org.postgresql">
					<xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
				</driver>
		...    
			</drivers>

		<datasources>

#####5.3.1 DataSource/DB

- Deberemos instalar primero el driver de la base de datos relacional a la plataforma del Application Server, para esto deben crear 2 carpetas (postgres y main): 
 
		<jboss-as-7.1.0.Final>/modules/org  (debiendo quedar la siguiente estructura)
   
		<jboss-as-7.1.0.Final>/modules/org/postgresql/main 

- Dentro de la carpeta main copiar el archivo **postgresql-9.1-902.jdbc4.jar** y crear un archivo **module.xml** cuyo contenido debe ser:
   
		<?xml version="1.0" encoding="UTF-8"?>
		<module xmlns="urn:jboss:module:1.0" name="org.postgresql">
			<resources>
				<resource-root path="postgresql-9.1-902.jdbc4.jar"/>
			</resources>
			<dependencies>
				<module name="javax.api"/>
				<module name="javax.transaction.api"/>
			</dependencies>
		</module>

- Crear base de datos BD "formrender" utilizando el cliente de preferencias, si el esquema (base de datos) no se encuentra creado, la aplicaci&oacute;n no levantar&aacute; correctamente.

- Ejecutar los scripts de estructura y datos en la BD creada, estos est&aacute;n ubicados en FormRender/sql/ y son:

	- FormRender/sql/estructuras.sql (crea las tablas en la bd)
	- FormRender/sql/datos.sql (inserción de formularios)
	- FormRender/sql/estructura-DELTA_incremental-[DATESTAMP ULTIMA ACTUALIZACIÓN].sql (actualiza la estructura de la bd)
	- FormRender/sql/datos-DELTA_incremental-[DATESTAMP ULTIMA ACTUALIZACIÓN].sql (actualiza los datos en la bd)

<span id="54"/></span>

####5.4 Descarga, Compilaci&oacute;n y Ejecuci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>

- Este proyecto usa git para control de versiones y esta disponible en github. Para bajarse el proyecto, ejecutar

		git clone git@cluster.softwarepublico.gob.ar:cnc_ict.git
   
- Realizar una copia del archivo de configuraci&oacute;n base (**FormRender/src/main/resources/formrender.properties**) y completar las variables con la informaci&oacute;n correcta:
	- Configurar path destino de los archivos de especificacion de formularios (.xml) en archivo de propiedades 
	
		xmlForms.destination (Ej. xmlForms.destination=/var/cnc)	
	
	- Configurar ip/port server de donde se tomar&aacute;n listas externas tales como geogr&aacute;ficas y prestadores en archivo de propiedades. Se debe tener en cuenta si usa o no encriptaci&oacute;n y si este tiene un contexto habilitado diferente al / (ROOT)
	
		FormRender/src/main/resources/formrender.properties	

	
		list.remote.host (Ej. list.remote.host=54.232.16.128)
		list.remote.port (Ej. list.remote.port=8080)
		list.remote.secure (Ej. list.remote.secure=false)
		list.remote.context (Ej. list.remote.context=/)

	- Estos mismos pasos deben realizarse para configurar tambi&eacute;n el servidor de persistencia de persistencia bajo el prefijo **submit.remote**.
		- submit.remote.draft (Ej. /deposition/draft)
		- submit.remote.final= (Ej. /deposition)
	- El archivo formrender.properties modificado se deber&aacute; pegar en el directorio de configuraci&oacute;n de Jboss (**<jboss-as-7.1.0.Final>/standalone/configuration/**)
	- Se debe evitar en todas las variables ubicar "slashes finales" (submit.remote.draft = /example <- INCORRECTO)

- Situarse en la ra&iacute;z del directorio del c&oacute;digo y ejecutar 

	$>mvn clean package

Esto genera un archivo war en "FormRender/target/FormRender.war"
	
- Deployar el archivo "FormRender.war" generado, para ello en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>/standalone/deployments
   
- Iniciar el server (standalone.bat en windows o standalone.sh unix)
   
- Acceder desde un browser a la direcci&oacute;n.
	
	http://<localhost:8080>/FormRender/

La p&aacute;gina de inicio muestra un listado de los formularios xml y html (columnas URL y XML respectivamente). Haciendo click en cada uno de ellos se pueden visualizar.


<span id="6"/></span>

##6. Uso FormRender <span style="font-size:8px;">([Arriba](#0))</span>
En esta secci&oacute;n explicaremos la forma de subir archivos y la forma posterior de accederlos, una vez que la aplicaci&oacute;n ya est&aacute; levantada.

<span id="61"/></span>
####6.1 Subida de archivos XML
En la p&aacute;gina de inicio: 
http://<localhost:8080>/FormRender/

Ir al men&uacute; ***Formularios***, muestra un listado de los formularios xml existentes actualmente. Permite tambi&eacute;n la b&uacute;squeda mediante filtros: c&oacute;digo, nombre y/o archivo.

Ejemplo: 

*Nombre*: Redes

*C&oacute;digo*: C1.3

*Archivo*: C1.3.xml 

**bot&oacute;n Buscar** y retorna lista de formularios seg&uacute;n filtros aplicados.

Para subir un formulario: dar click en **bot&oacute;n Nuevo**, lo que abrir&aacute; la pantalla de creaci&oacute;n con un formulario vac&iacute;o, por lo tanto completar con los datos correspondientes y Guardar. A modo de ejemplo:


<table>
<thead>
<tr style="text-align:left;">
<th colspan="3">Crear Formulario</th>
</tr>
</thead>
<tbody>
<tr>
<td>C&oacute;digo &uacute;nico:</td>
<td>C1.1</td>
</tr>
<tr>
<td>Nombre / Descripci&oacute;n:</td>
<td>Áreas de prestación de servicios</td>
</tr>
<tr>
<td>Versi&oacute;n:</td>
<td>C1.001-V001-MAY13</td>
</tr>
<tr>
<td >Archivo (xml) *</td>
<td>|Examinar|  C1.1.xml</td>
</tr>
<tr>
<td>Xsl **</td>
<td>Seleccione ...</td>
</tr>
</table>
<tbody>

<table>
<thead>
<tr style="text-align:center;background-color:#507EA8;color:white;">
<th>Guardar</th>
<th>Cancelar</th>
</tr>
</thead>
</table>

*Nota*: 

*El Archivo xml deseado a subir debe seleccionarlo mediante bot&oacute;n Examinar

**El xsl que se usar&aacute; para la transformaci&oacute;n / renderizaci&oacute;n debe seleccionarlo de el/los xsls existentes.

<span id="62"/></span>
####6.2 Subida de archivos XSL
En la p&aacute;gina de inicio: 
http://<localhost:8080>/FormRender/

Ir al men&uacute; ***Xsl***, muestra un listado de los xsl existentes actualmente. Permite tambi&eacute;n la b&uacute;squeda mediante filtros: nombre y/o archivo.

Ejemplo: 

*Nombre/Descripci&oacute;n*: Para uso


*Archivo*: formCnc.xsl

**bot&oacute;n Buscar** y retorna lista de xsls seg&uacute;n filtros aplicados.

Para subir un nuevo xsl dar click en **bot&oacute;n Nuevo**, lo que abrir&aacute; la pantalla de creaci&oacute;n con un formulario vac&iacute;o, por lo tanto completar con los datos correspondientes y Guardar. A modo de ejemplo:


<table>
<thead>
<tr style="text-align:left;">
<th colspan="3">Crear Xsl</th>
</tr>
</thead>
<tbody>
<tr>
<td>Nombre / Descripci&oacute;n:</td>
<td>Xsl para uso de ...</td>
</tr>
<tr>
<td>Xls Versi&oacute;n:</td>
<td>C1.001-V001-MAY13</td>
</tr>
<tr>
<td >Archivo (xsl) *</td>
<td>|Examinar|  formCnc.xsl</td>
</tr>
</table>
<tbody>

<table>
<thead>
<tr style="text-align:center;background-color:#507EA8;color:white;">
<th>Guardar</th>
<th>Cancelar</th>
</tr>
</thead>
</table>

*Nota*: 

*El Archivo xsl que se quiere subir debe seleccionarlo mediante bot&oacute;n Examinar


<span id="63"/></span>
####6.3 Acceso / Llamada al servicio

* **Para llamar / renderizar un formulario desde la misma aplicaci&oacute;n** 

ir a http://<localhost:8080>/FormRender/  

Ir a Men&uacute; **Formularios**, de la lista ubicar la fila corespondiente al formulario del cual se se quiere visualizar el html y dar al **Bot&oacute;n Ver** de la columna **Html**, con lo cual se renderizar&aacute; el formulario seleccionado.  


**Formularios**

<table>
<thead>
<tr style="text-align:center;background-color:66CCFF;">
<th>C&oacute;d</th>
<th>Nombre</th>
<th>Archivo</th>
<th>Xml</th>
<th>Html</th>
</tr>
</thead>
<tr>
<td>C1.1</td>
<td>Áreas de prestaci&oacute;n de servicios</td>
<td>C1.1.xml</td>
<td>|Ver|</td>
<td style="text-align:center;background-color:#507EA8;color:white">Ver</td>
</tr>
<tr>
<td>C1.2</td>
<td>Interconexi&oacute;n</td>
<td>C1.2.xml</td>
<td>|Ver|</td>
<td style="text-align:center;background-color:#507EA8;color:white">Ver</td>
</tr>
</table>

* **Para llamar / renderizar un formulario directamente**

Acceder a la siguiente direcci&oacute;n básica, que renderiza el formulario seg&uacute;n su c&oacute;digo que lo identifica:

http://<localhost:8080>/FormRender/formulario/display.xhtml?**id**=**C1.1**

donde **id** es el par&aacute;metro **c&oacute;digo &uacute;nico de identificaci&oacute;n** del formulario, en este caso cuyo valor es C1.1


Ahora bien, hay casos especiales a saber, que se detallar&aacute;n a a continuaci&oacute;n. Antes destacamos que cuando se haga referencia a la palabra ***grilla***, lo que significa es que el formulario contiene una tabla a la que se le van agregando registros, tantos como se quiera. Cuando se haga referencia a la palabra ***grupo*** significa que es que hay una agrupaci&oacute;n conceptual con título (fijo o variable) que internamente contiene cuadros de texto o listas de selecci&oacute;n que tienen relaci&oacute;n entre s&iacute;.   

Entonces, según como haya sido definido el formulario en el XML:

- Si el formulario no posee grilla y tampoco grupos que se repitan n veces, se har&aacute; una llamada normal, una llamada b&aacute;sica, es decir que s&oacute;lo necesitaremos el par&aacute;metro **id**:

	*http://<localhost:8080>/FormRender/formulario/display.xhtml?id=C1.1*

- Si el formulario xml a renderizar no posee grupos que se repitan n veces, pero s&iacute; tiene grilla, la llamada se har&aacute; como sigue:

	*http://<localhost:8080>/FormRender/formulario/display.xhtml?id=C1.1&repeat=1*

- Si el formulario xml a renderizar tiene agrupaciones que se repiten n veces con t&iacute;tulos variables y grilla, a la llamada se le deben agregar otros par&aacute;metros 

	*http://<localhost:8080>/FormRender/formulario/display.xhtml?id=C1.1&title =Enero,Febrero,Marzo&repeat=3*

	donde el significado de los par&aacute;metros es:
	
	**id** = C&oacute;digo &uacute;nico que identifica al formulario.
	
	**repeat**=cantidad de veces que se repite un bloque en un formulario.
	
	**title**=Lista separada por comas de los t&iacute;tulos que llevar&aacute;n los bloques que se repiten.


- Igual que en el caso anterior, si se tratara de un formulario que tiene agrupaciones que se repiten n veces con t&iacute;tulos variables y no posee grilla:

	*http://<localhost:8080>/FormRender/formulario/display.xhtml?id=C1.1&title =Enero,Febrero,Marzo&repeat=3*

	donde, como antes, el significado de los par&aacute;metros es:
	
	**id** = C&oacute;digo &uacute;nico que identifica al formulario.
	
	**repeat**=cantidad de veces que se repite un bloque en un formulario.
	
	**title**=Lista separada por comas de los t&iacute;tulos que llevar&aacute;n los bloques que se repiten.

Si se quiere mostrar la versi&oacute;n del formulario al renderizar, adicionalmente puede pasarse el par&aacute;metro con nombre **versi&oacute;n** y valor, el que corresponda. Si no se informa, el proceso de transformaci&oacute;n busca si el formulario guardado en BD tiene esa informaci&oacute;n y la tomar&aacute; de all&iacute;.

<span id="7"/></span>

##7. Mantenimiento FormRender <span style="font-size:8px;">([Arriba](#0))</span>
Para futuras mejoras que se requieran o necesiten hacer a la aplicaci&oacute;n, indicamos la ubicaci&oacute;n de los servicios en el c&oacute;digo fuente de modo de facilitar su b&uacute;squeda

<span id="71"/></span>
####7.1 Servicios

*TransformationService*: transformaci&oacute;n xml a html  v&iacute;a xsl.

Ubicaci&oacute;n:

	src/main/java/ar/com/cuyum/cnc/service/TransformationService

*RelayService*: creada para llamada de servicios de listas externas. Principalmente para evitar limitaciones por CSRF.

Ubicaci&oacute;n:

	src/main/java/ar/com/cuyum/cnc/service/RelayService

*RelayRest*: accede a datos de listas externas-remotas, submit y  recuperaci&oacute;n de formularios.

Ubicaci&oacute;n: 

	src/main/java/ar/com/cuyum/cnc/service/rest/RelayRest

------------	
