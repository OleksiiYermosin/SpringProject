package ua.training.springproject.utils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterPredicate {

    private final List<Predicate> predicates = new ArrayList<>();

    public <T> FilterPredicate add(T field, Function<T, Predicate> function){
        if(field != null && !field.toString().isEmpty()){
            System.out.println("Field" + field);
            predicates.add(function.apply(field));
        }
        return this;
    }

    public Predicate build(){
        return ExpressionUtils.allOf(predicates);
    }

    public static FilterPredicate builder(){
        return new FilterPredicate();
    }

}
