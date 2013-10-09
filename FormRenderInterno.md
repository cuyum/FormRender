Autor: [Cuyum](http://www.cuyum.com/) 
        
Fecha:	13 de Septiembre, 2013

#FormRender

Detalle dise&ntilde;o formularios en XML:

* T&iacute;tulo del formulario. Definir entre tags **<h:title/>**

 		<h:head>
    	<h:title>Formulario C1.1 - Áreas de prestación de servicios</h:title>
		<model>
			...
		</model>
  		</h:head>


* Submit de un formulario. Definir entre tags **< submission/>** 

		<h:head>
    		...
    		<model>
      		<submission action="/api/deposition/draft" method="form-data-post"/>
      		<instance>
			...
			</instance>
			...
			</model>

	El proceso tomar&aacute; del archivo de configuraci&oacute;n formrender.properties lo que se haya definido en:
	
	**submit.remote.host**. Ejemplo: http\://weddelltest.cnc.gob.ar o ip
	 
	**submit.remote.port**. Ejemplo: 80
	
	Por lo tanto al momento de darle submit al formulario irá a:

		http://weddelltest.cnc.gob.ar:8080/api/deposition/draft

* Acceso del formulario a Listas Externas. Se trata por ejemplo de combos que traen  o deben mostrar datos de una BD externa a la de FormRender (recordar que la BD de FormRender solo guarda registros correspondientes a formularios Xml y Archivos de transformaci&pacute;n; xsl), por lo tanto todos los datos ajenos a la aplicaci&oacute;n est&aacute;n en una BD y proyecto aparte. En nuestro caso tenemos el proyecto Localizacion que se estuvo usando en FormRender, y que guarda datos de provincias, departamentos, localidades, prestadores, entre otros, a los que se accede mediante un Rest Service, haciendo POST y que nos devolver&aacute; un JSON.

 Se define en el xml en la parte de **binds** el path desde donde traer&aacute; los datos a mostrar en los combos. Se debe especificar la **url** y si la lista depende de la selecci&oacute;n de un valor en una lista/combo anterior entonces tambi&eacute;n hay que especificar **depends**. En el ejemplo que sigue *provincia* no depende de nadie, s&oacute;lo lleva url, en cambio *partido* depende de provincia por lo tanto agregamos el depends tal y como se muestra a continuaci&oacute;n:
  
		<h:head>
    		...
    		...
      	  <instance>
			...
		  </instance>

		  <bind constraint="url=/localizacion/rest/localizaciones/provincias" nodeset="/C1.1/areasprest/areas/provincia" required="true()" type="select1"/>
	      <bind constraint="url=/localizacion/rest/localizaciones/partidos and depends=/C1.1/areasprest/areas/provincia" nodeset="/C1.1/areasprest/areas/partido" required="true()" type="select1"/>
	      <bind nodeset="/C1.1/areasprest/areas/partido_otro" relevant=" /C1.1/areasprest/areas/partido =9999" required="true()" type="string"/>
	      <bind constraint="url=/localizacion/rest/localizaciones/localidades and depends=/C1.1/areasprest/areas/partido" nodeset="/C1.1/areasprest/areas/localidad" required="true()" type="select1"/>
	      <bind nodeset="/C1.1/areasprest/areas/localidad_otro" relevant=" /C1.1/areasprest/areas/localidad =9999" required="true()" type="string"/>
		</model>
  		</h:head>


La ip y port, las tomar&aacute; de formrender.properties

**list.remote.host**=http\://54.232.16.128

**list.remote.port**=8080

Quedando como resultado el acceso para provincias, como ejemplo, as&iacute;:

	http://54.232.16.128:8080/localizacion/rest/localizaciones/provincias

* **Relevant**: indica si un campo debe mostrarse o no en un formulario seg&uacute;n cumpla o no una condici&oacute;n. Se define en la secci&oacute;n de **binds** 

Por ejemplo en *localidades* el campo *otras localidades* ser&aacute; visible s&iacute; y s&oacute;lo s&iacute; el valor de la localidad seleccionada en el combo de Localidades es igual a 9999

	...
	<bind nodeset="/C1.1/areasprest/areas/localidad_otro" relevant=" /C1.1/areasprest/areas/localidad =9999" required="true()" type="string"/>
	...

Un ejemplo un poco m&aacute;s complicado ser&iacute; si necesitamos usar condiciones OR AND. 

	<bind nodeset="/C1.3/puntos/tipotendido" relevant=" /C1.3/puntos/tipomedio/medio =FO or  /C1.3/puntos/tipomedio/medio =Otros"/>

* **cuit**. Podemos definir en el xml que un campo sea de tipo cuit, en cuyo caso se aplicar&aacute; una validaci&oacute;n de que sean s&oacute;lo n&uacute;meros y cuya cantidad debe ser 11. Se define en la secci&oacute;n de **binds** y se puede poner simplemente **cuit** o **type=cuit** siendo ambos v&aacute;lidos.

		< bind constraint="url=/localizacion/rest/localizaciones/prestadores and cuit" nodeset="/C1.14/meses/mes/ll/llOperador1" required="true()" type="select1"/>

* **mask**. Podemos definir en el xml que un campo tenga m&aacute;scara. Se define en la secci&oacute;n de **binds** y se puede poner simplemente **mask** o **type=mask** siendo ambos v&aacute;lidos. Ejemplo:

	    < bind constraint="mask=9.999.999,99" nodeset="/D1.2/inter/interconexion/capacidad" required="true()" type="decimal"/>

* **Restricciones mayor que menor que**. Se pueden definir restricciones a un campo int o decimal del tipo >= &oacute; =< &oacute; > &oacute; < tal y como se muestra en el ejemplo que sigue:

		< bind constraint=".&gt;=0 and .&lt;=9999999" jr:constraintMsg="Ingrese entero entre 0 y 9999999" nodeset="/C1.2/inter/interconexion/capacidad" required="true()" type="int"/>

* **Mensajes de error de validaci&oacute;n** de un campo. Cada campo tiene en el proceso de validaci&oacute;n un mensaje por defecto, pero si se quiere uno espec&iacute;fico se lo debe definir en **jr:constraintMsg**. Observar:

		< bind constraint=".&gt;=0 and .&lt;=9999999" jr:constraintMsg="Ingrese entero entre 0 y 9999999" nodeset="/C1.2/inter/interconexion/capacidad" required="true()" type="int"/>

* **Tooltips o Hints**. Se pueden definir tooltips para campos o para el t&iacute;tulo de un formulario mediante el uso de **hint** en la secci&oacute;n **<h:body>**. 

Ejemplo para tooltip de el t&iacute;tulo de un formulario. Observar que se hace uso de **appareance="tooltip"** y **hint**
		
		...
		</model>
	  	</h:head>
	  	<h:body>
	    	<input appearance="tooltip" ref="/C1.2/titulo">
	      	<hint>Debe informar cada uno de sus puntos de interconexión: ubicación geográfica de origen y destino, capacidad y prestador con el cual se conecta.</hint>
	    </input>


Ejemplo para un campo, que en este caso es un combo y para el cu&aacute;l s&oacute;lo hace falta definir el **hint**:

	<select1 appearance="minimal" ref="/C1.3/puntos/equipamiento/tecnologia">
            <label>Tecnología</label>
            <hint>Elegir desde la tabla el tipo de tecnología implementada. En el caso de no figurar, elegir la opción &quot;otros&quot; y completar manualmente.</hint>
            <item>
              <label>Seleccione una opción</label>
              <value>-1</value>
            </item>
            <item>
              <label>SDH</label>
              <value>SDH</value>
            </item>
            <item>
              <label>DWDM</label>
              <value>DWDM</value>
            </item>
            <item>
              <label>PDH</label>
              <value>PDH</value>
            </item>
            <item>
              <label>IP</label>
              <value>IP</value>
            </item>            
          </select1>

* Grilla. Si se quiere definir una grilla hay que usar **repeat** y **appareance="grilla"**

		<repeat appearance="grilla" nodeset="/C1.3/puntos">
        <group appearance="col4 well-white" ref="/C1.3/puntos/lugarOrigen">
          <label>Ubicación Origen</label>
          <select1 appearance="minimal" ref="/C1.3/puntos/lugarOrigen/provinciaOrigen">
            <label>Provincia</label>
            <hint>Provincia</hint>
            <item>
              <label>Seleccione una opción</label>
              <value>-1</value>
            </item>
          </select1>


* Otros usos de **appareance**. Tambi&eacute;n puede usarse para enumerar clases de estilos de la hoja de estilos asociada que tenga el xsl. Separar por espacios. Ejemplo:

		<group appearance="col1 well-white variable" ref="/F1.26/meses/mes">
          ...
        </group>

* T&iacute;tulo variable para un grupo que se repite. Se puede definir que un grupo se repita n veces y que el t&iacute;tulo de ese grupo venga dado por par&aacute;metro, es decir que ser&aacute; variable. Para ello debemos especificar que el t&iacute;tulo es variable usando **{title}** as&iacute;:

		<group appearance="col1 well-white variable" ref="/F1.26/meses/mes">
          <label>{title}</label>
          <input ref="/F1.26/meses/mes/hombres">
            <label>Hombres</label>
            <hint>Hombres</hint>
          </input>
          <input ref="/F1.26/meses/mes/mujeres">
            <label>Mujeres</label>
            <hint>Mujeres</hint>
          </input>
        </group>
 


------------	
