package com.sparta.blackwhitedeliverydriver.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blackwhitedeliverydriver.config.TestSecurityConfig;
import com.sparta.blackwhitedeliverydriver.dto.BasketAddRequestDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketGetResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketResponseDto;
import com.sparta.blackwhitedeliverydriver.dto.BasketUpdateRequestDto;
import com.sparta.blackwhitedeliverydriver.entity.UserRoleEnum;
import com.sparta.blackwhitedeliverydriver.mock.user.MockUser;
import com.sparta.blackwhitedeliverydriver.service.BasketService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BasketController.class)
@Import(TestSecurityConfig.class)
class BasketControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BasketService basketService;

    @Autowired
    ObjectMapper mapper;

    private static final String BASE_URL = "/api/v1";

    @Test
    @DisplayName("장바구니 담기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void addProductToBasket_success() throws Exception {
        //given
        UUID productId = UUID.randomUUID();
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 담기 실패 : 권한이 CUSTOMER 아닌 경우")
    @MockUser(role = UserRoleEnum.OWNER)
    void addProductToBasket_fail() throws Exception {
        //given
        UUID productId = UUID.randomUUID();
        UUID basketId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        int quantity = 2;
        BasketAddRequestDto request = BasketAddRequestDto.builder()
                .productId(productId)
                .storeId(storeId)
                .quantity(quantity)
                .build();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.addProductToBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(request);
        mvc.perform(post(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 빼기 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void removeProductFromBasket_success() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", basketId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 빼기 실패 : 허용하지 않은 권한으로 호출")
    @MockUser(role = UserRoleEnum.OWNER)
    void removeProductFromBasket_fail() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        BasketResponseDto response = new BasketResponseDto(basketId);

        //when
        when(basketService.removeProductFromBasket(any(), any())).thenReturn(response);

        //then
        mvc.perform(delete(BASE_URL + "/baskets/{basketId}", basketId.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER, username = "user")
    void getBaskets_success() throws Exception {
        // given
        String username = "user";
        String storeName = "store";
        String productName = "product";
        Integer quantity = 2;
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        BasketGetResponseDto responseDto = BasketGetResponseDto.builder()
                .basketId(basketId)
                .username(username)
                .storeId(storeId)
                .productId(productId)
                .quantity(quantity)
                .build();

        // 페이징 관련 설정
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "storeName"));
        Page<BasketGetResponseDto> pageResponse = new PageImpl<>(List.of(responseDto), pageRequest, 1);

        when(basketService.getBaskets(eq(username), eq(0), eq(10), eq("storeName"), eq(true)))
                .thenReturn(pageResponse);

        // when & then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "storeName")
                        .param("isAsc", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].basketId").value(basketId.toString()))
                .andExpect(jsonPath("$.content[0].username").value(username))
                .andExpect(jsonPath("$.content[0].storeId").value(storeId.toString()))
                .andExpect(jsonPath("$.content[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.content[0].quantity").value(quantity))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("장바구니 조회 실패: 유효하지 않은 사용자")
    @MockUser(role = UserRoleEnum.OWNER)
    void getBaskets_fail_invalidUser() throws Exception {
        // given
        String username = "user";
        String storeName = "store";
        String productName = "product";
        Integer quantity = 2;
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();

        BasketGetResponseDto responseDto = BasketGetResponseDto.builder()
                .basketId(basketId)
                .username(username)
                .storeId(storeId)
                .productId(productId)
                .quantity(quantity)
                .build();

        // 페이징 관련 설정
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "storeName"));
        Page<BasketGetResponseDto> pageResponse = new PageImpl<>(List.of(responseDto), pageRequest, 1);

        when(basketService.getBaskets(eq(username), eq(0), eq(10), eq("storeName"), eq(true)))
                .thenReturn(pageResponse);

        // when & then
        mvc.perform(get(BASE_URL + "/baskets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "storeName")
                        .param("isAsc", "true"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("장바구니 수정 성공")
    @MockUser(role = UserRoleEnum.CUSTOMER)
    void updateBasket_success() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        int quantity = 2;
        BasketResponseDto response = BasketResponseDto.builder()
                .basketId(basketId)
                .build();
        //when
        when(basketService.updateBasket(any(), any())).thenReturn(response);

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").exists());
    }

    @Test
    @DisplayName("장바구니 수정 실패 : 허용하지 않은 권한")
    @MockUser(role = UserRoleEnum.OWNER)
    void updateBasket_fail() throws Exception {
        //given
        UUID basketId = UUID.randomUUID();
        int quantity = 2;

        //when
        when(basketService.updateBasket(any(), any()))
                .thenReturn(BasketResponseDto.builder()
                        .basketId(basketId)
                        .build());

        //then
        String body = mapper.writeValueAsString(BasketUpdateRequestDto.builder()
                .basketId(basketId)
                .quantity(quantity)
                .build());

        mvc.perform(put(BASE_URL + "/baskets")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}