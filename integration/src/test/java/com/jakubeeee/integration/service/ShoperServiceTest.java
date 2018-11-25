package com.jakubeeee.integration.service;

import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ShoperProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static com.jakubeeee.common.utils.LangUtils.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
public class ShoperServiceTest {

    @InjectMocks
    ShoperService shoperService;

    @Test
    public void convertToCommonProductsTest() {
        var shoperProduct1 = new ShoperProduct("1", "code1", "15", "10", "aaa");
        var shoperProduct2 = new ShoperProduct("2", "code2", "40", "12", "bbb");
        var shoperProduct3 = new ShoperProduct("3", "code3", "70", "15", "ccc");
        var shoperProducts = new ArrayList<ShoperProduct>(toList(shoperProduct1, shoperProduct2, shoperProduct3));
        var commonProduct1 = new CommonProduct("code1", null, "15", "10", "aaa", null);
        commonProduct1.addParam("SHOPER_ID", "1");
        var commonProduct2 = new CommonProduct("code2", null, "40", "12", "bbb", null);
        commonProduct2.addParam("SHOPER_ID", "2");
        var commonProduct3 = new CommonProduct("code3", null, "70", "15", "ccc", null);
        commonProduct3.addParam("SHOPER_ID", "3");
        var expectedResult = new ArrayList<>(toList(commonProduct1, commonProduct2, commonProduct3));
        var result = shoperService.convertToCommonProducts(shoperProducts);
        assertThat(result, is(equalTo(expectedResult)));
    }
}