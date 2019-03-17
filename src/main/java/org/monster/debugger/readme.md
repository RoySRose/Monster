#debugging 사용법

1.구조    
*PreBouUxmanager.java 에 필요한 정보 추가 후 ScreenUx.java 에서 메소드 호출.
*화면 상에서 Left, Mid, Right 에 정보 표시 가능.  
*UxDrawConfig 객체 생성 후 정적 팩토리 메서드 호출.   
*String, Field, Method 로 호출 가능  
  
    
2.사용  
*출력하고자 하는 값일 String 일 경우 newInstanceStringType(위치,타이틀,값,칼라)->
uxDrawConfig = UxDrawConfig.newInstanceStringType("L", "reaverInMyBaseFrame",
				DrawingUtils.framesToTimeString(EnemyBuildTimer.Instance().reaverInMyBaseFrame), UxColor.CHAR_WHITE);  
				
*출력하고자 하는 값일 Field 일 경우 newInstanceFiledType(위치,타이틀,클래스,field,칼라)->  
newInstanceFiledType("M",key,UxDrawConfig.classMap.get(key.toUpperCase()),value,UxColor.CHAR_GREEN)   

*출력하고자 하는 값일 Method 일 경우 newInstanceFiledType(위치,타이틀,클래스,method명,칼라)->  
newInstanceMethodType("R", "* group size", LagObserver.class, "groupsize",UxColor.CHAR_WHITE);


 