package org.monster.build.base;


/// 건설위치 초안 결정 정책
/// 향후 적진 길목, 언덕 위 등 추가
public enum SeedPositionStrategy {
    NoLocation,                 /// < Default
    MainBaseLocation,            ///< 아군 베이스
    SecondMainBaseLocation,        ///< My 2nd Base
    MainBaseBackYard,            ///< 아군 베이스 뒷편
    FirstChokePoint,            ///< 아군 첫번째 길목
    FirstExpansionLocation,        ///< 아군 첫번째 앞마당
    SecondChokePoint,            ///< 아군 두번째 길목
    SeedPositionSpecified,        ///< 별도 지정 위치
    NextExpansionPoint,            ///< 다음 멀티 위치
    NextSupplePoint,            ///< 다음 서플 위치
    LastBuilingPoint,            ///< 최종 건물 위치
    FreeSpaceUsing,                ///< 남는 여백 활용
    //LastBuilingPoint2,			///< 최종 건물 위치
    getLastBuilingFinalLocation ///< 완전 더이상 없다
}
