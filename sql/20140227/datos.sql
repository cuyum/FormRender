INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoI','anexoI','anexoI-V001-FEB14','anexoI.xml', 0,1);
INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoII','anexoII','anexoII-V001-FEB14','anexoII.xml', 0,1);
INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoIII','anexoIII','anexoIII-V001-FEB14','anexoIII.xml', 0,1);
INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoIV','anexoIV','anexoIV-V001-FEB14','anexoIV.xml', 0,1);
INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoV','anexoV','anexoV-V001-FEB14','anexoV.xml', 0,1);
INSERT INTO formulario (codigo, nombre, formVersion, archivo, version,xsltransform_id) VALUES ('anexoVI','anexoVI','anexoVI-V001-FEB14','anexoVI.xml', 0,1);

UPDATE formulario SET parametrosurl = 'id=anexoI' WHERE (codigo = 'anexoI');
UPDATE formulario SET parametrosurl = 'id=anexoI&repeat=1' WHERE (codigo = 'anexoI');

UPDATE formulario SET parametrosurl = 'id=anexoII' WHERE (codigo = 'anexoII');
UPDATE formulario SET parametrosurl = 'id=anexoIII' WHERE (codigo = 'anexoIII');
UPDATE formulario SET parametrosurl = 'id=anexoIV' WHERE (codigo = 'anexoIV');
UPDATE formulario SET parametrosurl = 'id=anexoV' WHERE (codigo = 'anexoV');
UPDATE formulario SET parametrosurl = 'id=anexoVI' WHERE (codigo = 'anexoVI');