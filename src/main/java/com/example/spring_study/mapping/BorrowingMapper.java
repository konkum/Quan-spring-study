package com.example.spring_study.mapping;

import com.example.spring_study.model.Borrowing;
import com.example.spring_study.model.payload.BorrowingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(uses = {EmployeeMapper.class}, componentModel = "spring")
public interface BorrowingMapper {
    @Mapping(source = "employee", target = "employee")
    BorrowingResponse borrowingToBorrowingResponse(Borrowing borrowing);

    List<BorrowingResponse> borrowingsToResponses(List<Borrowing> borrowings);

    default Page<BorrowingResponse> borrowingPageToResponsePage(Page<Borrowing> borrowingPage) {
        List<BorrowingResponse> responses = borrowingsToResponses(borrowingPage.getContent());
        return new PageImpl<>(
                responses,
                borrowingPage.getPageable(),
                borrowingPage.getTotalElements()
        );
    }
}
