package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //기본 읽기로 정하고
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입

    @Transactional//예외인경우만 이렇게 설정 읽기 모드 x
    public Long join(Member member) {
        vaildDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void vaildDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미존재하는 회원입니다");
        }
    }

    public List<Member> findMembers() {

        return memberRepository.findAll();

    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
