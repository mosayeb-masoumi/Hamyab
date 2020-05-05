package com.rahbarbazaar.hamyab.api_error;

import com.rahbarbazaar.hamyab.network.ErrorProvider;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ErrorsMessage422 parseError422(Response<?> response){
        Converter<ResponseBody, ErrorsMessage422> converter=
                ErrorProvider
                        .retrofit.
                        responseBodyConverter(ErrorsMessage422.class,new Annotation[0]);
        ErrorsMessage422 error;

        try{
            error=converter.convert(response.errorBody());

        }catch (IOException e){
            return  new ErrorsMessage422();
        }
        return error;
    }

//    public static APIError403 parseError403(Response<?> response){
//        Converter<ResponseBody, APIError403> converter=
//                ErrorProvider
//                        .retrofit.
//                        responseBodyConverter(APIError403.class,new Annotation[0]);
//        APIError403 error;
//
//        try{
//            error=converter.convert(response.errorBody());
//
//        }catch (IOException e){
//            return  new APIError403();
//        }
//        return error;
//    }
//
//    public static APIError406 parseError406(Response<?> response){
//        Converter<ResponseBody, APIError406> converter=
//                ErrorProvider
//                        .retrofit.
//                        responseBodyConverter(APIError406.class,new Annotation[0]);
//        APIError406 error;
//
//        try{
//            error=converter.convert(response.errorBody());
//
//        }catch (IOException e){
//            return  new APIError406();
//        }
//        return error;
//    }
}
