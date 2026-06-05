# 解压public
if [ ! -f "./public.zip" ];then
  echo "./public.zip压缩包不存在！"
else
  mkdir -p ./temp
  mv -f ./public/assets/config.js ./temp/
  rm -rf ./public
  unzip ./public.zip -d ./
  mv -f ./temp/config.js ./public/assets/
  rm -rf ./temp
  rm -rf ./public.zip
  echo "./public.zip解压成功！"
fi

