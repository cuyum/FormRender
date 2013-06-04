FormRender
===============

Permite el Ingreso de Formularios en archivos .xml que contienen su especificacion (tipos de datos, requeridos o no, etc).

Transformaci�n xml => html.

1.Objetivo del documento 
-------------------------

Especificar los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicacion.

2.Configuracion
---------------
Componentes necesarios para poder ejecutar la aplicación:
*	JDK 1.6.x
*	Jboss-as-7.1.0.Final
*	Maven 3.0.4
*	PostgreSQL 1.16

2.1 Requisitos minimos
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

Crear base de datos BD formrender

A continuacion se especifica el datasource que debe ser usado, en este caso para una BD Postgres.

Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml en la secci�n <datasources> 
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

2.1) Al levantarse el JBoss, se generaran las tablas correspondientes.

Para desplegar una aplicacion nueva, desde cero, el persistence.xml deber� estar en modo create, 

teniendo presente que en cada arranque esta configuracion elimina los datos que esten cargados.

<property name="hibernate.hbm2ddl.auto" value="create"/>, 

Hace la carga inicial y creacion de tablas automaticamente.


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
	
3) Configurar ip/port server de donde se tomarán listas externas tales como geográficas y prestadores en archivo de 
	propiedades.
	
	FormRender\src\main\resources\formrender.properties	
	
	Propiedades:
	
	list.remote.host y list.remote.port
	
	Ej.	list.remote.host=http://54.232.16.128
	list.remote.port=8080

4) Situarse en la raiz del directorio del codigo y ejecutar 
	$>mvn clean package
	Esto genera un archivo war en "FormRender/target/FormRender.war"
	
5) Deployar el archivo "FormRender.war" generado, para ello
   en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>\standalone\deployments
   
6) Iniciar el server (standalone.bat en windows o standalone.sh unix)
   
7) Acceder desde un browser a la direccion. Ej
	
	http://<localhost:8080>/FormRender/
	
	La página de inicio muestra un listado de los formularios que se presentaron de muestra para esta entrega
	xml y html (columnas URL y XML respectivamente). Haciendo click en cada uno de ellos se pueden visualizar.
	
