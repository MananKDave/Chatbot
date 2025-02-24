from rest_framework import serializers
from app.models import signup, alldata


class signupserializers(serializers.ModelSerializer):
    class Meta:
        model = signup
        fields = ('name', 'email', 'password', 'cpassword')


class alldataserializers(serializers.ModelSerializer):
    class Meta:
        model = alldata
        fields = ('name', 'message', 'reply', 'links', 'date', 'time')