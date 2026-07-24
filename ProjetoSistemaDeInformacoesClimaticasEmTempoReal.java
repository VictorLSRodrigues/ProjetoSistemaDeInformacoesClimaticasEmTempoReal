import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

//javac --module-path "%PATH_TO_FX%" --add-modules org.json,javafx.controls,javafx.media,javafx.web
//java --module-path "%PATH_TO_FX%" --add-modules org.json,javafx.controls,javafx.media,javafx.web

public class ProjetoSistemaDeInformacoesClimaticasEmTempoReal{
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		System.out.print("Digite o nome da cidade: ");
		String cidade = sc.nextLine();//ler cidade no teclado

		try{
			String dadosClimaticos = getDadosClimaticos(cidade);//retorna um JSON

			//codigo 1006 siginifica localização não e encontrada.
			if(dadosClimaticos.contains("\"code\":1006")){// \"code\":1006 representa "code:1006" // escape caracter
				System.out.println("Localização não encontrada. Por favor,tente novamente.");
			}else{
				imprimirDadosClimaticos(dadosClimaticos);
			}

		}catch(Exception e){
			System.out.println(e.getMessage());
		}

	}
	public static String getDadosClimaticos(String cidade) throws Exception{
		String apiKey = Files.readString(Paths.get("api-key.txt")).trim(); 

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key="+ apiKey +"&q="+formataNomeCidade;
		HttpRequest request = HttpRequest.newBuilder()// começa a construção  de uma nova solicitação http
			.uri(URI.create(apiUrl))//este metodo define o URI da solicitação http.
			.build();//finaliza a construção da solicitação http.

			// criar objeto eviar solicitaçoes http e receber respostas http, para acessar o site da WeatherAPI
			HttpClient client = HttpClient.newHttpClient();

			// agora vamos enviar reqisisçoes http e receber respostas http, comunicar com o site da API Meterologica.
			HttpResponse<String> resposta = client.send(request, HttpResponse.BodyHandlers.ofString());

			return resposta.body();//retorna os dados meteorologicos obtidos no site da API(WeatherAPI)
	}
	// metodo para imprimir os dados meteorologicos de forma organizada
	public static void imprimirDadosClimaticos(String dados){
		//System.out.println("Dados originais(JSON) obtidos no site meteorologico"+ dados); //dados originais

		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

		//Extrai os dados da localização 
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		// extrai dados adicionais
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");
		int umidade = informacoesMeteorologicas.getInt("humidity");
		float velocidadeVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		// extrai a data e a hora da string retorndad pela API
		String dataHoraS = informacoesMeteorologicas.getString("last_updated");

		//imprimir informações atuais
		System.out.println("Informações Meteorolicas"+ cidade +", "+pais);
		System.out.println("Data e hora: "+dataHoraS);
		System.out.println("Temperatura Atual: "+ temperaturaAtual+"ºC");
		System.out.println("Sensação termica: "+sensacaoTermica+"ºC");
		System.out.println("Condição tempo: "+condicaoTempo);
		System.out.println("Umidade: "+ umidade+"%");
		System.out.println("Velocidade vento: "+ velocidadeVento+" Km/h");
		System.out.println("Presão Atmosferica: "+pressaoAtmosferica+" mb");
	}
} 
