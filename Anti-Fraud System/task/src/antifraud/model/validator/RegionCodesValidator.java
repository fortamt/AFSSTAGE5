package antifraud.model.validator;

import antifraud.model.RegionCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class RegionCodesValidator implements ConstraintValidator<RegionCodesConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<RegionCodes> regionCodes = new ArrayList<>((EnumSet.allOf(RegionCodes.class)));
        boolean flag = false;
        for(RegionCodes codes : regionCodes){
            if(codes.name().equals(value)){
                flag =true;
            }
        }
        return flag;
    }
}
