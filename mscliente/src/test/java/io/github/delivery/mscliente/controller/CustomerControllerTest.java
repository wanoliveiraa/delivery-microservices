package io.github.delivery.mscliente.controller;


import io.github.delivery.mscliente.MsclienteApplicationTests;
import io.github.delivery.mscliente.constants.Constants;
import io.github.delivery.mscliente.dto.CustomerResponse;
import io.github.delivery.mscliente.model.Customer;
import io.github.delivery.mscliente.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest extends MsclienteApplicationTests {

    @Autowired
    private CustomerRepository  customerRepository;

    private Customer customer;

    @BeforeEach
    void clean() throws IOException {
        customer = readJsonFileAndConvert("json/create_customer_request.json", Customer.class);
        customerRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void createCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )       .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.phone").value("98999999999"))
                .andExpect(jsonPath("$.personType").value("Pessoa Física"))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(1))
                .andExpect(jsonPath("$.documents[0].type").value("CPF"))
                .andExpect(jsonPath("$.documents[0].value").value("98765432100"));

    }

    @Test
    @WithMockUser
    void updateCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        var createResult = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        var created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                CustomerResponse.class
        );

        doRequest(
                put("/api/v1/customers/" + created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.phone").value("98999999999"))
                .andExpect(jsonPath("$.personType").value("Pessoa Física"))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(1))
                .andExpect(jsonPath("$.documents[0].type").value("CPF"))
                .andExpect(jsonPath("$.documents[0].value").value("98765432100"));
    }

    @Test
    @WithMockUser(roles = Constants.IS_ADMIN)
    void findAllCustomerSuccessfully() throws Exception {

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated());


        doRequest(
                get("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        )       .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].userId").exists())
                .andExpect(jsonPath("$.content[0].name").value("Cliente Teste"))
                .andExpect(jsonPath("$.content[0].phone").value("98999999999"))
                .andExpect(jsonPath("$.content[0].personType").value("Pessoa Física"))
                .andExpect(jsonPath("$.content[0].addresses").isArray())
                .andExpect(jsonPath("$.content[0].addresses.length()").value(2))
                .andExpect(jsonPath("$.content[0].documents").isArray())
                .andExpect(jsonPath("$.content[0].documents.length()").value(1))
                .andExpect(jsonPath("$.content[0].documents[0].type").value("CPF"))
                .andExpect(jsonPath("$.content[0].documents[0].value").value("98765432100"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));

    }


    @Test
    @WithMockUser()
    void findCustomerIdSuccessfully () throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse created =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        CustomerResponse.class
                );

        UUID customerId = created.id();

        doRequest(
                get("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.phone").value("98999999999"))
                .andExpect(jsonPath("$.personType").value("Pessoa Física"))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(1))
                .andExpect(jsonPath("$.documents[0].type").value("CPF"))
                .andExpect(jsonPath("$.documents[0].value").value("98765432100"));
    }

    @Test
    @WithMockUser
    void findCustomerByUserIdSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated());

        doRequest(
                get("/api/v1/customers/user/" + userId)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.phone").value("98999999999"))
                .andExpect(jsonPath("$.personType").value("Pessoa Física"))
                .andExpect(jsonPath("$.addresses").isArray())
                .andExpect(jsonPath("$.addresses.length()").value(2))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(1));
    }

    @Test
    @WithMockUser
    void findCustomerByDocumentSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated());

        doRequest(
                get("/api/v1/customers/document/98765432100")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Cliente Teste"))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(1))
                .andExpect(jsonPath("$.documents[0].type").value("CPF"))
                .andExpect(jsonPath("$.documents[0].value").value("98765432100"));
    }

    @Test
    @WithMockUser(roles = Constants.IS_ADMIN)
    void deleteCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerResponse.class
        );

        doRequest(
                delete("/api/v1/customers/permanent/" + created.id())
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        doRequest(
                get("/api/v1/customers/" + created.id())
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteCustomerSoftSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerResponse.class
        );

        doRequest(
                delete("/api/v1/customers/" + created.id())
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        doRequest(
                get("/api/v1/customers/" + created.id())
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllCustomerWithoutAdminRole() throws Exception {

        doRequest(
                get("/api/v1/customers")
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void deleteCustomerPermanentWithoutAdminRole() throws Exception {

        UUID id = UUID.randomUUID();

        doRequest(
                delete("/api/v1/customers/permanent/" + id)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void findCustomerByIdNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        doRequest(
                get("/api/v1/customers/" + id)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Client not found with id: " + id));
    }
}
