package br.com.sistema.controllers;

import javax.net.ssl.SSLException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import br.com.sistema.util.RestPageImpl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Controller
public class MainController {

	public WebClient createWebClient() throws SSLException {
//		String url = "https://sistema.atacarejoceasasdobrasil.com.br/";
		String url = "https://localhost:8443/";

		SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

		String basicAuthHeader = "basic " + Base64Utils.encodeToString(("joav" + ":" + "123456").getBytes());

		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(url)
				.defaultCookie("cookieKey", "cookieValue")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader)
				.build();
	}

	@GetMapping("/")
	public String main(Model model) throws SSLException {

//		RestTemplate restTemplate = new RestTemplate();
//		String url = "https://sistema.atacarejoceasasdobrasil.com.br/";
		String url = "https://localhost:8443/";

//		ResponseEntity<List<Object>> response = restTemplate.getForEntity(url, new List<Object.class>);

		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

		String basicAuthHeader = "basic " + Base64Utils.encodeToString(("joav" + ":" + "123456").getBytes());

		WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(url)
				.defaultCookie("cookieKey", "cookieValue")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader).build();

		Mono<RestPageImpl<?>> response = webClient
				.get()
				.uri("/api/produto")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<RestPageImpl<?>>() {
				});
		Page<?> ss = response.block();
//		ss.forEach(s -> System.out.println(ss.getContent()));
		model.addAttribute("p", ss);
//		System.out.println(ss);
//		System.out.println(uriSpec.toString());
//		String[] objects = response.block();
//		ObjectMapper mapper = new ObjectMapper();
//		Arrays.stream(objects)
//		  .map(object -> mapper.convertValue(object, Object.class))
//		  .map(Object::toString)
//		  .collect(Collectors.toList());
//		System.out.println(response.block());
//		System.out.println(uriSpec);
		return "index";
	}

	@GetMapping("/produto/{id}")
	public ModelAndView getOne(@PathVariable("id") Long id) throws SSLException {
		ModelAndView view = new ModelAndView("produto");
		Mono<Object> c = createWebClient()
		.get()
		.uri("/api/produto/"+id)
		.accept(MediaType.APPLICATION_JSON)
		.retrieve()
		.bodyToMono(Object.class);
		
//		System.out.println(c.block());
		view.addObject("produto", c.block());
		
		return view;
	}

}
