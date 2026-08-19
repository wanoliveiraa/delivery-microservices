package io.github.delivery.mscliente.controller;


import io.github.delivery.mscliente.MsclienteApplicationTests;
import io.github.delivery.mscliente.constants.Constants;
import io.github.delivery.mscliente.dto.CustomerResponse;
import io.github.delivery.mscliente.model.Customer;
import io.github.delivery.mscliente.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void createCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "CLIENT")
                        .content(objectMapper.writeValueAsString(customer))
        )
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
    void updateCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        var createResult = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
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
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
                        .content(objectMapper.writeValueAsString(customer))
        )
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
    void findAllCustomerSuccessfully() throws Exception {

        doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated());


        doRequest(
                get("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
                        .content(objectMapper.writeValueAsString(customer))
        )
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
    void findCustomerIdSuccessfully () throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "CLIENT")
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
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", "CLIENT")
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
    void findCustomerByUserIdSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse created =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        CustomerResponse.class
                );


        doRequest(
                get("/api/v1/customers/user/" + created.userId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
        )
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
    void findCustomerByDocumentSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
                        .content(objectMapper.writeValueAsString(customer))
        )
                .andExpect(status().isCreated())
                .andReturn();

        CustomerResponse created =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        CustomerResponse.class
                );


        doRequest(
                get("/api/v1/customers/document/98765432100")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
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
    void deleteCustomerSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
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
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        doRequest(
                get("/api/v1/customers/" + created.id())
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCustomerSoftSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        MvcResult result = doRequest(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
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
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
        )
                .andDo(print())
                .andExpect(status().isNoContent());

        doRequest(
                get("/api/v1/customers/" + created.id())
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_ADMIN)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllCustomerWithoutAdminRole() throws Exception {

        UUID userId = UUID.randomUUID();

        doRequest(
                get("/api/v1/customers")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCustomerPermanentWithoutAdminRole() throws Exception {

        UUID id = UUID.randomUUID();

        doRequest(
                delete("/api/v1/customers/permanent/" + id)
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    void findCustomerByIdNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        doRequest(
                get("/api/v1/customers/" + id)
                        .header("X-User-Id", UUID.randomUUID().toString())
                        .header("X-User-Role", Constants.ROLE_CLIENT)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Client not found with id: " + id));
    }
}
