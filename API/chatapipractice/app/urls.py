from django.contrib import admin
from django.urls import path, include
from rest_framework.authtoken.views import obtain_auth_token
from .views import TestView, TestViewRegister, TestViewLogin, TestViewHistory, TestViewdata, TestViewHistoryLocation

urlpatterns = [
    path('app-auth/', include('rest_framework.urls')),
    path('admin/', admin.site.urls),
    path('testview/', TestView.as_view(), name='test'),
    path('testviewdata/', TestViewdata.as_view(), name='testdata'),
    path('testviewregister/', TestViewRegister.as_view(), name='Register'),
    path('testviewlogin/', TestViewLogin.as_view(), name='Login'),
    path('testviewhistory/', TestViewHistory.as_view(), name='History'),
    path('testviewhistorylocation/', TestViewHistoryLocation.as_view(), name='HistoryLocation'),
    path('app/token/', obtain_auth_token, name='obtain-token')
]
