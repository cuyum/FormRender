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
	- FormRender/sql/formulariosCNC.sql (inserci&oacute;n de formularios de CNC)

<span id="54"/></span>

####5.4 Descarga, Compilaci&oacute;n y Ejecuci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>

- Este proyecto usa git para control de versiones y esta disponible en github. Para bajarse el proyecto, ejecutar

		git clone git@cluster.softwarepublico.gob.ar:cnc2220.git
   
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
	- El archivo modificado se deber&aacute; pegar en el directorio de configuraci&oacute;n de Jboss (**<jboss-as-7.1.0.Final>/standalone/configuration/**)

- Situarse en la ra&iacute;z del directorio del c&oacute;digo y ejecutar 

	$>mvn clean package

Esto genera un archivo war en "FormRender/target/FormRender.war"
	
- Deployar el archivo "FormRender.war" generado, para ello en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>/standalone/deployments
   
- Iniciar el server (standalone.bat en windows o standalone.sh unix)
   
- Acceder desde un browser a la direcci&oacute;n.
	
	http://<localhost:8080>/FormRender/

La p&aacute;gina de inicio muestra un listado de los formularios xml y html (columnas URL y XML respectivamente). Haciendo click en cada uno de ellos se pueden visualizar.

------------	
