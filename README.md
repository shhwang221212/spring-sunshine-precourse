# spring-sunshine-precourse


## getCoordinatesByCityName(enum city)
도시 이름을 넣으면 위도와 경도를 반환하는 메소드
output: coordinate double DTO

## ???
시/군, 구, 동 까지 볼 수 있는 어쩌고 저쩌고

## getWeatherByCoordinates(double latitude, double longitude)
위도와 경도를 넣으면 위치에 해당하는 온도, 습도, 체감온도, 날씨코드, 요약을 반환하는 메소드
output: current.temperature_2m, relative_humedity_2m, apperent_temperature, weather_code, summary DTO

## getSummaryByInfo(current.temperature_2m, relative_humedity_2m, apperent_temperature, weather_code)
온도, 습도, 체감온도, 날씨코드로 요약을 생성하여 반환하는 메소드
output: summary string

## getDescriptionByWeatherCode(int weather_code)
날씨코드와 매핑되는 설명을 반환하는 메소드
output: string

