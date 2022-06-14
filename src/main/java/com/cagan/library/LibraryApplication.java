package com.cagan.library;

import com.cagan.library.config.ApplicationProperties;
import com.cagan.library.config.EBookProperties;
import com.cagan.library.config.ProfileConstants;
import com.cagan.library.integration.stripe.service.StripePaymentIntentService;
import com.stripe.Stripe;
import org.apache.commons.lang3.StringUtils;
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

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    private final StripePaymentIntentService paymentService;

    @Autowired
    public LibraryApplication(Environment env, StripePaymentIntentService paymentService) {
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

    @Value("${stripe.secret_key}")
    private String apiKey;

    @Bean
    public CommandLineRunner run() {
        return args -> {
            Stripe.apiKey = apiKey;
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
//            CustomerCreateParams customerCreateParams = CustomerCreateParams
//                    .builder()
//                    .setName("Demo Customer")
//                    .setEmail("customer@demo.com")
//                    .setDescription("Demo address for demo customer")
//                    .build();
//
//            Customer customer = Customer.create(customerCreateParams);
//
//            log.info("Created [Customer: {}]", customer.getId());
//
//            ProductCreateParams productCreateParams = ProductCreateParams
//                    .builder()
//                    .setName("IPhone 12")
//                    .setDescription("Apple IPhone 12")
//                    .setType(ProductCreateParams.Type.GOOD)
//                    .setActive(true)
//                    .build();
//
//            Product product = Product.create(productCreateParams);
//
//            log.info("Created [Product: {}]", product.getId());
//
//            PriceCreateParams priceCreateParams = PriceCreateParams
//                    .builder()
//                    .setCurrency("usd")
//                    .setUnitAmount(3000L)
//                    .setProduct(product.getId())
//                    .build();
//
//            Price price = Price.create(priceCreateParams);
//
//            log.info("Created [Price: {}]", price.getId());
//
//            InvoiceItemCreateParams invoiceItemCreateParams = InvoiceItemCreateParams.builder()
//                    .setCustomer(customer.getId())
//                    .setPrice(price.getId())
//                    .build();
//
//            InvoiceItem invoiceItem = InvoiceItem.create(invoiceItemCreateParams);
//            log.info("Created [Invoice Item: {}]", invoiceItem.getId());
//
//            InvoiceCreateParams invoiceCreateParams = InvoiceCreateParams.builder()
//                    .setCustomer(customer.getId())
//                    .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
//                    .setDaysUntilDue(30L)
//                    .build();
//
//            Invoice invoice = Invoice.create(invoiceCreateParams);
//            log.info("Created [Invoice: {}]", invoice.getId());
//
//            InvoiceSendInvoiceParams invoiceSendInvoiceParams = InvoiceSendInvoiceParams.builder().build();
//            Invoice sentInvoice = invoice.sendInvoice(invoiceSendInvoiceParams);
//            log.info("Invoice [Invoice: {}] has been sent", invoice.getId());
        };
    }
}
