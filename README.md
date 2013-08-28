
FormRender
===============

Permite el Ingreso de Formularios en archivos .xml que contienen su especificaci&oacute;n (tipos de datos, requeridos o no, etc).

Transformaci&oacute;n xml => html.

<span id="0"/></span>

&Iacute;ndice
=============
1. [Objetivos del documento](#1)
2. [Introducci&oacute;n](#2)
3. [Tecnología](#3)
4. [Vista Lógica](#4)
	- [Capa Web](#41)
	- [Capa Servicios](#42)
	- [Capa Procesador XSLT](#43)
	- [Capa Seguridad ](#44)
	- [Capa Transaccional](#45)
	- [Capa Integración](#46)

5. [Compilación, Instalación y Ejecución](#6)
	- [Listado de componentes necesarios](#61)
	- [Requisitos mínimos](#62)
	- [Configuración](#63)
	- [Descarga, Compilación y Ejecución](#64)

----------------
<span id="1"/></span>

1. Objetivo del documento <span style="font-size:8px;">([Arriba](#0))</span>
--------------------------

Presentar una visión general de las características de la aplicación FormRender y sus aspectos técnicos de mayor relevancia, especificando las caracter&iacute;sticas t&eacute;cnicas y de arquitectura que tiene la plataforma, entre ellos los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicaci&oacute;n.

----------------
<span id="2"/></span>

2. Introducci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>
-----------------------
La funci&oacute;n principal de FormRender es poder generar formularios HTML a partir de una especificaci&oacute;n basada en el estandar [xForms](http://www.w3.org/MarkUp/Forms/). 
Los documentos xForms son documentos XML los cuales a trav&eacute;s de una pantalla administrativa se cargan un con un identificador para luego ser recuperados e integrados a otras aplicaciones. FormRender internamente utiliza un procesador XSL para a trav&eacute;s de un XSLT (Procesador de Transformación XSL) generar el código HTML correspondiente con sus correspondientes estilos CSS y funcionalidad JavaScript.

----------------
<span id="3"/></span>

3. Tecnología <span style="font-size:8px;">([Arriba](#0))</span>
-------------
Es un sistema transaccional cuya interface a usuario es facilitada mediante el uso de tecnología web compatible con las últimas versiones de los Browsers de Internet (Internet Explorer, Firefox, Chrome).
El uso de esta tecnología permitirá al sistema ser accesible desde cualquier lugar que cuente con conexión a Internet, podrá ser instalado y desplegado en servidores propios, externos o en “Clouds” lo que facilita la escalabilidad de la aplicación en caso de ser necesaria mayor carga de trabajo. 

El sistema está construido utilizando plataforma y estándares de desarrollo JEE 6 (Java Enterprise Edition). Como implementación de este estándar se utiliza el stack tecnológico porvisto por JBoss 7.1.0 https://www.jboss.org/jbossas/ cuyas implementaciones principales son las siguientes:

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

Como tecnología de soporte para el mantenimiento de la información se utiliza base de datos relacional [PostgreSQL](http://www.postgresql.org/)

----------------
<span id="4"/></span>

4. Vista Lógica <span style="font-size:8px;">([Arriba](#0))</span>
----------------

<center>
<table border="1">
<tr><td style="text-align:center;background-color:FF9933;" colspan="4">Web Servlet/JSF2</td></tr>
<tr><td style="text-align:center;background-color:66CCFF;" colspan="4">Servicios <br/>CDI/POJO</td></tr>
<tr>
<td style="text-align:center;background-color:66CC66;">JPA2 Transaccional</td>
<td style="text-align:center;background-color:66CC66;">Procesador XSLT</td>
<td style="text-align:center;background-color:66CC66;">Integración <br/> JAX-WS/JAX-RS</td>
<td style="text-align:center;background-color:66CC66;">Seguridad Conector<br/> POJO</td>
</tr>
</table>
</center>

<span id="41"/></span>

###Capa Web <span style="font-size:8px;">([Arriba](#0))</span>
El objetivo de la capa web es proveer una interfaz de acceso al sistema para el usuario final. En esta capa se utiliza la implementación de JSF2 Primefaces para construir las interfaces a usuarios y Servlet para resolver la función principal del sistema que es devolver un formulario especificado en HTML.

<span id="42"/></span>

###Capa Servicios <span style="font-size:8px;">([Arriba](#0))</span>
Esta capa brinda un nivel de abstracción para acceso a la lógica de la aplicación, proveyendo así un conjunto de servicios uniformes y transparentes a los clientes mediante el uso CDI. Esta tecnología permite ofrecer POJOs como servicios y brinda facilidades de integración entre la capa de presentación, los servicios de negocio y los módulos restantes.

<span id="43"/></span>

###Capa Procesador XSLT <span style="font-size:8px;">([Arriba](#0))</span>
Representa el corazón de la aplicación, tiene la responsabilidad de transformar, leyendo de una base de formularios, la especificación de un formulario xForm en un HTML con sus estilos CSS y librerías JavaScript. Se está utilizando Saxon como engine XSLT para realizar la transformación.

<span id="44"/></span>

###Capa Seguridad <span style="font-size:8px;">([Arriba](#0))</span>
Esta capa brinda una interface para que la aplicación pueda resolver sus necesidades de autorización y autenticación. Existe una implementación default que resuelve estas cuestiones accediendo a un repositorio Redis.

<span id="45"/></span>

###Capa Transaccional <span style="font-size:8px;">([Arriba](#0))</span>
Su objetivo es brindar de una manera homogénea y transparente, mediante el uso del estándar de persistencia JPA2, el acceso a la información al resto de la aplicación independizándolo de la base de datos física con la que interactúa.

<span id="46"/></span>

###Capa Integración <span style="font-size:8px;">([Arriba](#0))</span>
Provee interfaces para acceder a datos externos a la aplicación que neecsiten los formularios. Las implementaciones de estas interfaces se realiz+an mediante WebServices SOAP (JAX-WS) o Rest Services (JAX-RS) 

----------------

<span id="5"/></span>

5. Compilación, Instalación y Ejecución <span style="font-size:8px;">([Arriba](#0))</span>
----------------
En ésta sección se detalla todo lo necesario para compilar, instalar o deployar y ejecutar la plataforma. Se asume que los siguientes componentes, necesarios para dichas tareas, se encuentran instalados y corriendo normalmente en el sistema operativo.

<span id="51"/></span>
####5.1 Listado de componentes necesarios para poder ejecutar la aplicaci&oacute;n:

- JDK 1.6.x ([Guía de instalación](https://help.ubuntu.com/community/Java))
- Jboss-as-7.1.0.Final ([Descarga](http://www.jboss.org/jbossas/downloads/))
- Maven 3.0.4 ([Descarga](http://maven.apache.org/download.cgi),[Instalación](http://maven.apache.org/download.cgi#Unix-based_Operating_Systems_Linux_Solaris_and_Mac_OS_X))
- PostgreSQL 9.1 ([Guía de instalación](https://help.ubuntu.com/13.04/serverguide/postgresql.html))
- Git (Solo para entorno de desarrollo, [Guía de instalación](https://help.ubuntu.com/community/Git))

<span id="52"/></span>
####5.2 Requisitos Mínimos:

Es necesario tener instalados (al menos) 2Gb de ram.

<span id="53"/></span>
####5.3 Instalación y Configuraci&oacute;n de entorno <span style="font-size:8px;">([Arriba](#0))</span>

Agregar al archivo __<jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml__

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
 
		<jboss-as-7.1.0.Final>\modules\org  (debiendo quedar la siguiente estructura)
   
		<jboss-as-7.1.0.Final>\modules\org\postgresql\main 

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

- Crear base de datos BD "formrender" utilizando el cliente de preferencias, si el esquema (base de datos) no se encuentra creado, la aplicación no levantará correctamente.

- Ejecutar los scripts de estructura y datos en la BD creada, estos están ubicados en FormRender/sql/ y son:
	- FormRender/sql/estructuras.sql (crea las tablas en la bd)
	- FormRender/sql/formulariosCNC.sql (inserción de formularios de CNC)

<span id="54"/></span>

####5.4 Descarga, Compilación y Ejecución <span style="font-size:8px;">([Arriba](#0))</span>

- Este proyecto usa git para control de versiones y esta disponible en github. Para bajarse el proyecto, ejecutar

		git clone git@cluster.softwarepublico.gob.ar:cnc2220.git
   
- Configurar path destino de los archivos de especificacion de formularios (.xml) en archivo de propiedades __FormRender\src\main\resources\formrender.properties	__
	
		xmlForms.destination (Ej. xmlForms.destination=/var/cnc)	
	
- Configurar ip/port server de donde se tomar&aacute;n listas externas tales como geogr&aacute;ficas y prestadores en archivo de propiedades. Esto hace referencia al localizacion.war en donde est&aacute; el acceso a las listas externas.
	
		FormRender\src\main\resources\formrender.properties	

	
		list.remote.host (Ej. list.remote.host=http://54.232.16.128)
		list.remote.port (Ej. list.remote.port=8080)

- Situarse en la ra&iacute;z del directorio del c&oacute;digo y ejecutar 

	$>mvn clean package

Esto genera un archivo war en "FormRender/target/FormRender.war"
	
- Deployar el archivo "FormRender.war" generado, para ello en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>\standalone\deployments
   
- Iniciar el server (standalone.bat en windows o standalone.sh unix)
   
- Acceder desde un browser a la direcci&oacute;n.
	
	http://<localhost:8080>/FormRender/

La página de inicio muestra un listado de los formularios xml y html (columnas URL y XML respectivamente). Haciendo click en cada uno de ellos se pueden visualizar.

------------	
