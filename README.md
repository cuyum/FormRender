cnc
===============

Ingreso de espeficaciones de formularios en archivos .xml 

1.Objetivo del documento 
-------------------------

Especificar los requerimientos de sistema necesarios para poder instalar y ejecutar la aplicacion.

2.Configuracion
---------------
Componentes necesarios para poder ejecutar la aplicación:
*	JDK 1.6.x
*	Jboss-as-7.1.0.Final
*	Maven 3.0.4
*	MySQL-5.1

2.1 Requisitos minimos
1) Es necesario tener instalados (al menos) 2Gb de ram.

2.2	Configuracion JBoss

Agregar al archivo <jboss-as-7.1.0.Final>\standalone\configuration\standalone.xml



2.3	Configuracion BD

...

2.1) Al levantarse el JBoss, se generan y/o actualizan las tablas correspondientes.

Si se quiere desplegar una aplicacion nueva, desde cero sin datos cargados, configurar el 
persistence.xml en modo create, teniendo presente que en cada arranque esta configuracion elimina los datos que 
esten cargados.

<property name="hibernate.hbm2ddl.auto" value="create"/>, 

Hace la carga inicial y creacion de tablas automaticamente.

2.2) L aplicacion esta configurada en modo update, ya que ya contiene tablas creadas y datos cargados.

ver archivo <aplicacion>src/main/resources/META_INF/persistence.xml observar que en modo update deberia configurarse del siguiente modo

<property name="hibernate.hbm2ddl.auto" value="update"/>

3.Despliegue
------------

3.1 Pasos

1) Este proyecto usa git para control de versiones y esta disponible en github. 
   Para bajarse el proyecto, hacer git clone https://github.com/cuyum/cnc
	
2) Situarse en la raiz del directorio y ejecutar 
	$>mvn clean package
	Esto genera un archivo war en "cnc/target/cnc.war"
	
3) Deployar el archivo "cnc/target/cnc.war", para ello
   en JBoss 7.1.0 copiar el archivo al directorio <jboss-as-7.1.0.Final>\standalone\deployments 
   
4) Acceder desde un browser a la direccion 
	
	localhost:8080/cnc/
