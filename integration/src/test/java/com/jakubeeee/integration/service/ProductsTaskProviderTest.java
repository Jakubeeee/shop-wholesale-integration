package com.jakubeeee.integration.service;

import com.jakubeeee.common.model.ChangeRegistry;
import com.jakubeeee.integration.model.CommonProduct;
import com.jakubeeee.integration.model.ProductMatchingResult;
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
public class ProductsTaskProviderTest {

    @InjectMocks
    ProductsTaskProvider productsTaskProvider;

    @Test
    public void matchProductsFromShopAndDataSourceTest() {
        var product1 = new CommonProduct("code1", "name1", "20", "50", "abcdefg", null);
        var product2 = new CommonProduct("code2", "name2", "50", "23,50", "hijklmn", null);
        var product3 = new CommonProduct("code3", "name3", "30", "10", "eaneanean", null);
        var product4 = new CommonProduct("code4", "name4", "10", "15", "eeeaaannn", null);
        var shopProducts = new ArrayList<CommonProduct>(toList(product1, product2, product3, product4));
        var product5 = new CommonProduct("code1", "name1", "5", "45", "abcdefg", null);
        var product6 = new CommonProduct("code2", "name2", "80", "24,50", "hijklmn", null);
        var product7 = new CommonProduct("code3", "name3", "25", "12", "naenaenae", null);
        var product8 = new CommonProduct("code5", "name5", "50", "14", "eeeaaannn", null);
        var dataSourceProducts = new ArrayList<CommonProduct>(toList(product5, product6, product7, product8));
        var mergedProduct1 = new CommonProduct("code1", "name1", "5", "45", "abcdefg", null);
        mergedProduct1.addProperty("MATCHED");
        var mergedProduct2 = new CommonProduct("code2", "name2", "80", "24,50", "hijklmn", null);
        mergedProduct2.addProperty("MATCHED");
        var mergedProduct3 = new CommonProduct("code3", "name3", "25", "12", "naenaenae", null);
        mergedProduct3.addProperty("MATCHED");
        var matchedProductsRegistry = new ArrayList<ChangeRegistry<CommonProduct>>();
        matchedProductsRegistry.add(new ChangeRegistry<>(product1, mergedProduct1));
        matchedProductsRegistry.add(new ChangeRegistry<>(product2, mergedProduct2));
        matchedProductsRegistry.add(new ChangeRegistry<>(product3, mergedProduct3));
        var expectedResult = new ProductMatchingResult();
        expectedResult.setMatchedProductsRegistry(matchedProductsRegistry);
        expectedResult.setUnmatchedUpdatableDataSourceProducts(toList(product4));
        expectedResult.setUnmatchedDataSourceProducts(toList(product8));
        ProductMatchingResult result = productsTaskProvider.matchProducts(
                shopProducts, dataSourceProducts);
        assertThat(result, is(equalTo(expectedResult)));
    }

}