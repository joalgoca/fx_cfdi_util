# fx_cfdi_util
Aplicación Desktop para la conversión de XML a PDF de CFDI´s de nómina e ingresos.

<h3>Objetivo</h3>
<p>Crear una aplicación desktop en Java que nos permita transformar másivamente XML de nómina e ingresos a PDF. 

<h3>Características</h3>
<ul>
<li>Conversión másiva XML (CFDI Nómina e Ingresos)  a PDF</li>
<li>Descarga masiva de XML al SAT</li>
<li>Portable</li>
<li>Almacenar información de los XML (en BD derby embebida en el proyecto)</li>
<li>Exportar información de los CFDI´s a Excel</li>
</ul>

<h3>Notas</h3>
<ul>
<li>El proyecto esta construido en Netbeans 8.1</li>
<li>Utiliza java 1.8</li>
<li>Aún no tengo la documentación del proyecto pero mi intensión es ser más cuidadoso de este importante detalle, por tal motivo empiezo a utilizar github.</li>
</ul>

<h3>Motivación</h3>
<p>Uno de los proyectos que he participado es sobre el timbrado de ingresos y nómina en Gobierno, en dicho proyecto muy comodamente
entregamos un archivo TXT y el PAC nos entrega el respectivo XML y PDF, sin embargo no todo es miel sobre hojuelas. Bastante a  menudo
la descarga del xml es correcta más sin embargo la del PDF no lo es. En principio les pediamos que lo descargaran de su buzón del SAT
una tarea bastante problematica cuando tienes cientos a miles de CFDI´s por mes, despues recomendamos algunas URL´s para su transformación
pero de las que encontramos pues no cumplian completamente nuestras espectativas pero por el momento resolvian el tema de generar el PDF uno a uno.</p>

<p>Finalmente en una semana de ocio en el trabajo, llego el momento de desarrollar esta aplicación, ya con los reportes en jasper reports
construidos pero con fuente de datos a una BD, el objetivo era invocar los datos desde un XML, despues de pelear 2 días y solo dar vueltas
en círculo, opte por mandar un objeto como parametro al .jasper, un poco de lectura por aquí y unos videos por alla resolví el tema de
mandar objetos complejos al .jasper. Por lo tanto primero se descompone el XML y se guarda en un Objeto Cfdi complejo con sus detalles el cual
 se manda como parametro al invocar el jasper reports.</p>
<p>Parecía que el asunto estaba arreglado ya tenia una aplicación de consola que iba a un directorio y de manera recursiva leía XML y los convertía
a PDF, y mi "grandioso cerebro" dijo hay que venderla, pero para eso habría que hacerle una interfaz gráfica y ver si ya hay algo en el mercado.</p>
<p>Al hacer el benchmarking o desilusión que ya había soluciones que no solo convertían másivamente XML a PDF, si no que tambien descargaban los XML
másivamente desde el SAT y los guardaban en una BD para su administración y exportación a excel por unos costos entre 400 hasta 1500 pesos por año.</p>
<p>No podía quedarme atrás y mi aplicación debía cumplir al menos con cada una de las características ofrecidas por la competencia, pero debia tener algo 
diferente debia ser portable.</p>
<p>Despues de diez dias de leer y experimentar obtuve algo muy similar a lo que ofrecen otras soluciones pero con unos PDF´s más atractivos
visualmente. Sin embargo 5 meses despues al día de hoy no he vendido ninguna, ademas que ya he visto otras soluciones gratuitas de mejor calidad 
que las que había visto en mi primer beanchmarking y que ademas son gratuitas.</p>
<p>Y esta es mi motivación final si ya hay aplicaciones gratuitas que convierten CFDI´s a PDF con caracteristicas extra como descarga masiva del SAT, almacenamiento de información 
y exportación a Excel, pues mi proyecto tiene que ir más alla y por tal motivo decidí abrirlo como <b>proyecto opensource</b> y compartirlo por este medio.</p>
<p>Yo no me puedo quejar de lo que me ha dejado este pequeño proyecto, ya que en practicamente en diez días aprendí:</p>
<ul>
<li>Invocar reportes de jasper reports con objetos como parametros</li>
<li>Construir mi primera aplicación con java FX2</li>
<li>Utilizar el webview de java FX2 para hacer mi primer webscrapping</li>
<li>Embeber derby BD a un proyecto desktop</li>
<li>Utilizar launch4j para convertir jar a exe</li>
</ul>
<p>Que si no me hubiera decidido a hacer el proyecto no obtenido dicha experiencia.</p>

