cnc
===============

Ingreso de espeficaciones de formularios en archivos .xml 

1.Objetivo del documento 
-------------------------

Especificar los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicación.

2.Configuración
---------------
Componentes necesarios para poder ejecutar la aplicación:
*	JDK 1.6.x
*	Jboss-as-7.1.0.Final
*	Maven 3.0.4
*	MySQL-5.1

2.1 Requisitos mínimos
1) Es necesario tener instalados (al menos) 2Gb de ram.

2.2	Configuración JBoss

Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml



2.3	Configuración BD

1) Instalar driver de base de datos en Application Server
   Ej: para instalar driver MySQL5 en Jboss 7.1.0 se debe copiar el archivo mysql-connector-java-5.1.18-bin.jar
   en el directorio <jboss-as-7.1.0.Final>\modules\com\mysql\main

2) Para ejecutar esta aplicación se debe crear una BD cnc. 

A continuación se especifica el datasource que debe ser usado, en este caso para una BD MySQL.
Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml en la sección <datasources> 
la siguiente entrada:

<datasource jndi-name="java:jboss/datasources/CncDS" pool-name="cnc" enabled="true" use-java-context="true">
                    <connection-url>jdbc:mysql://localhost:3306/cnc</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <user-name>username</user-name>
                        <password>password</password>
                    </security>
                    <validation>
                        <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
                    </validation>
                </datasource>
<drivers>    
    <driver name="mysql" module="com.mysql">
        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
    </driver>
</drivers>

y especificar usuario y password correspondiente

2.1) Al levantarse el JBoss, se generan y/o actualizan las tablas correspondientes.

Si se quiere desplegar una aplicacion nueva, desde cero sin datos cargados, configurar el 
persistence.xml en modo create, teniendo presente que en cada arranque esta configuración elimina los datos que 
están cargados.

<property name="hibernate.hbm2ddl.auto" value="create"/>, 

el siguiente paso no hace falta ya que al levantar la aplicacion por primera vez hace la carga inicial automáticamente.

2.2) Este paso es necesario porque la aplicación está configurada en modo update, ya que ya contiene tablas creadas y datos cargados.

ver archivo <aplicacion>src/main/resources/META_INF/persistence.xml observar que está en modo update

<property name="hibernate.hbm2ddl.auto" value="update"/>

3.Despliegue
------------

3.1 Pasos

1) Este proyecto usa git para control de versiones y está disponible en github. 
   Para bajarse el proyecto, hacer git clone https://github.com/cuyum/cnc
	
2) Situarse en la raiz del directorio y ejecutar 
	$>mvn clean package
	Esto genera un archivo war en "cnc/target/cnc-1.0.war"
	
3) Deployar el archivo "cnc/target/cnc.war", para ello
   en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>\standalone\deployments 
   
4) Acceder desde un browser a la direccion 
	
	localhost:8080/cnc/
