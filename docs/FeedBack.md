### Step1 PR FeedBack
 - Fixture은 동적 메소드보다는 상수화하여 사용할 것.

### Step2 PR FeedBack
 - Line insert 후 상행역, 하행역을 update 하는 로직에서 상행역, 하행역을 미리 할당하여 Line이 생성되도록 변경 (불필요한 update 로직 제거). 
 - 불변 객체의 변수는 final로 관리.
