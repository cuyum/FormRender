FormRender
===============

Permite el Ingreso de Formularios en archivos .xml que contienen su especificaci&oacute;n (tipos de datos, requeridos o no, etc).

Transformaci&oacute;n xml => html.

1.Objetivo del documento 
-------------------------

Especificar los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicaci&oacute;n.

2.Configuraci&oacute;n
---------------
Componentes necesarios para poder ejecutar la aplicaci&oacute;n:
*	JDK 1.6.x
*	Jboss-as-7.1.0.Final
*	Maven 3.0.4
*	PostgreSQL 1.16

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


3.Despliegue
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
	
