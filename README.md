

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
5. [Arquitectura](#5)
6. [Especificaciones De Desarrollo](#6)
7. [Especificaciones de Instalaci&oacute;n](#7)


<span id="1"/></span>

1. Objetivo del documento <span style="font-size:8px;">([Arriba](#0))</span>
--------------------------

Presentar una visión general de las características de la aplicación FormRender y sus aspectos técnicos de mayor relevancia, especificando las caracter&iacute;sticas t&eacute;cnicas y de arquitectura que tiene la plataforma, entre ellos los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicaci&oacute;n.

<span id="2"/></span>

2. Introducci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>
-----------------------
La funci&oacute;n principal de FormRender es poder generar formularios HTML a partir de una especificaci&oacute;n basada en el estandar [xForms](http://www.w3.org/MarkUp/Forms/). 
Los documentos xForms son documentos XML los cuales a trav&eacute;s de una pantalla administrativa se cargan un con un identificador para luego ser recuperados e integrados a otras aplicaciones. FormRender internamente utiliza un procesador XSL para a trav&eacute;s de un XSLT (Procesador de Transformación XSL) generar el código HTML correspondiente con sus correspondientes estilos CSS y funcionalidad JavaScript.

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

<span id="5"/></span>

5. Especificaciones T&eacute;cnicas <span style="font-size:8px;">([Arriba](#0))</span>
-----------------------------------

3. Configuraci&oacute;n <span style="font-size:8px;">([Arriba](#0))</span>
---------------
Componentes necesarios para poder ejecutar la aplicaci&oacute;n:
* JDK 1.6.x
* Jboss-as-7.1.0.Final
* Maven 3.0.4
* PostgreSQL 1.16

2.1 Requisitos m&iacute;nimos
1) Es necesario tener instalados (al menos) 2Gb de ram.

2.2	Configuracion JBoss

Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml

2.3	Configuracion BD  

2.3.1) Instalar driver de base de datos

   Para instalar driver de postgresql en jboss-as-7.1.0.Final se deben crear 2 carpetas (postgres y main) en 
   
   <jboss-as-7.1.0.Final>\modules\org  debiendo quedar la siguiente estructura:
   
   <jboss-as-7.1.0.Final>\modules\org\postgresql\main 
   
   Dentro de la carpeta main copiar el archivo postgresql-9.1-902.jdbc4.jar y crear un archivo module.xml cuyo
   contenido debe ser
   
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
2.3.2) Para ejecutar esta aplicacion se debe 

Crear base de datos BD "formrender"

A continuaci&oacute;n se especifica el datasource que debe ser usado, en este caso para una BD Postgres.

Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml en la secci&oacute;n <datasources> 
la siguiente entrada, especificando usuario y password correspondiente:

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
	...
	</datasources>
	...	   

2.1) Ejecutar los scripts de estructura y datos en la BD creada

Los scripts de carga inicial están ubicados en FormRender/sql/ y son:

FormRender/sql/estructuras.sql (crea las tablas en la bd)
FormRender/sql/formulariosCNC.sql (inserción de formularios de CNC)


3.Despliegue <span style="font-size:8px;">([Arriba](#0))</span>
------------

3.1 Pasos

1) Este proyecto usa git para control de versiones y esta disponible en github. 
   Para bajarse el proyecto, hacer git clone 
   
   git@cluster.softwarepublico.gob.ar:cnc2220.git
   
2) Configurar path destino de los archivos de especificacion de formularios (.xml)  en archivo de propiedades

	FormRender\src\main\resources\formrender.properties	
	
	Propiedad:xmlForms.destination
	
	Ej.	xmlForms.destination=/var/cnc
	
3) Configurar ip/port server de donde se tomar&aacute;n listas externas tales como geogr&aacute;ficas y prestadores en archivo de 
	propiedades. Esto hace referencia al localizacion.war en donde est&aacute; el acceso a las listas externas.
	
	FormRender\src\main\resources\formrender.properties	
	
	Propiedades:
	
	list.remote.host y list.remote.port
	
	Ej.	list.remote.host=http://54.232.16.128
	list.remote.port=8080
	

4) Situarse en la ra&iacute;z del directorio del c&oacute;digo y ejecutar 
	$>mvn clean package
	Esto genera un archivo war en "FormRender/target/FormRender.war"
	
5) Deployar el archivo "FormRender.war" generado, para ello
   en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>\standalone\deployments
   
6) Iniciar el server (standalone.bat en windows o standalone.sh unix)
   
7) Acceder desde un browser a la direcci&oacute;n. Ej
	
	http://<localhost:8080>/FormRender/
	
	La página de inicio muestra un listado de los formularios xml y html (columnas URL y XML respectivamente). Haciendo click en cada uno de ellos se pueden visualizar.
	
