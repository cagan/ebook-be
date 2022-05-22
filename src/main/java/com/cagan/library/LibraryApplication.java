package com.cagan.library;

import com.cagan.library.config.ApplicationProperties;
import com.cagan.library.config.EBookProperties;
import com.cagan.library.config.ProfileConstants;
import com.cagan.library.integration.stripe.CardPaymentObject;
import com.cagan.library.integration.stripe.PaymentIntentObject;
import com.cagan.library.integration.stripe.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Order;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.OrderCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class, EBookProperties.class})
@EnableAsync
public class LibraryApplication {

    private final Environment env;
    private static final Logger log = LoggerFactory.getLogger(LibraryApplication.class);

    private final StripePaymentService paymentService;
    @Autowired
    public LibraryApplication(Environment env, StripePaymentService paymentService) {
        this.env = env;
        this.paymentService = paymentService;
    }

    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());

        if (
                activeProfiles.contains(ProfileConstants.SPRING_PROFILE_DEVELOPMENT) &&
                        activeProfiles.contains(ProfileConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            log.error(
                    "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
                activeProfiles.contains(ProfileConstants.SPRING_PROFILE_DEVELOPMENT) &&
                        activeProfiles.contains(ProfileConstants.SPRING_PROFILE_CLOUD)
        ) {
            log.error(
                    "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LibraryApplication.class);
//        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
//        SpringApplication.run(LibraryApplication.class, args);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path"))
                .filter(StringUtils::isNotBlank)
                .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException exception) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        log.info(
                "\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );

        String configServerStatus = env.getProperty("configserver.status");
        if (configServerStatus == null) {
            configServerStatus = "Not found or not setup for this application";
        }

        log.info(
                "\n----------------------------------------------------------\n\t" +
                        "Config Server: \t{}\n----------------------------------------------------------",
                configServerStatus
        );
    }

//    @Value("${stripe.secret_key}")
//    private String apiKey;
//    @Bean
//    public CommandLineRunner run() {
//        return args -> {
//            Stripe.apiKey = apiKey;
//
//            Map<String, Object> card = new HashMap<>();
//            card.put("number", "4000000000000002");
//            card.put("exp_month", 5);
//            card.put("exp_year", 2023);
//            card.put("cvc", "314");
//            Map<String, Object> params = new HashMap<>();
//            params.put("type", "card");
//            params.put("card", card);
//            PaymentMethod paymentMethod = PaymentMethod.create(params);
//
//            PaymentIntentCreateParams createParams = PaymentIntentCreateParams
//                    .builder()
//                    .setCurrency("usd")
//                    .setAmount(100L)
//                    .setPaymentMethod(paymentMethod.getId())
//                    .setConfirm(false)
//                    .setDescription("Example payment intent description")
//                    .addPaymentMethodType("card")
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(createParams);
//            log.info("PAYMENT INTENT STATUS: {}", paymentIntent.getStatus());
//
//            try {
//                PaymentIntent paymentIntent1 = PaymentIntent.retrieve(paymentIntent.getId()).confirm();
//                log.info("PAYMENT INTENT STATUS: {}", paymentIntent1.getStatus());
//            } catch (CardException exception) {
//                System.out.println("INVALID CARD: " + exception.getMessage());
//            }

//            Stripe.apiKey = apiKey;
//            String status = Session.retrieve("cs_test_a1SUT9Y0BLl2QnL1aKuKw2PkqY7TajJDtYznHeJC6Qvs6QYr8zG9nsYzru")
//                    .getStatus();
//
//            log.info("------PAYMENT INTENT STATUS: {}------", status);
//
//            CardPaymentObject cpo = new CardPaymentObject();
//            cpo.setNumber("4242424242424242");
//            cpo.setExpMonth(5L);
//            cpo.setExpYear(2023L);
//            cpo.setCvc("314");
//
//            PaymentMethod paymentMethod = paymentService.createCardPaymentMethod(cpo);
//
//
//            PaymentIntentObject pio = new PaymentIntentObject();
//            pio.setPaymentMethodId(paymentMethod.getId());
//            pio.setPaymentMethodType(PaymentIntentObject.PaymentMethodType.card);
//            pio.setAmount(50L);
//            pio.setDescription("New attempt");
//            pio.setCurrency("usd");
//            pio.setConfirm(false);
//
//            PaymentIntent paymentIntent = paymentService.createPaymentIntent(pio);
//            log.info("PAYMENT INTENT STATUS: {}", paymentIntent.getStatus());
//
//            try {
//                PaymentIntent paymentIntent1 = paymentService.confirmPaymentIntent(paymentIntent.getId());
//                log.info("PAYMENT INTENT STATUS: {}", paymentIntent1.getStatus());
//            } catch (StripeException exception) {
//                System.out.println("INVALID CARD: " + exception.getMessage());
//            }
//        };
//    }
}
