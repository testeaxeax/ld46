#!/bin/bash

# Deployment key setup
openssl aes-256-cbc -k "$travis_key_password" -d -md sha1 -a -in ./.automation/automation.enc -out ../key;
echo "Host github.com" > ~/.ssh/config;
echo "  IdentityFile $(pwd)/../key" >> ~/.ssh/config;
chmod 400 ../key;
git remote set-url origin git@github.com:testeaxeax/ld46.git;
echo "github.com ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAq2A7hRGmdnm9tUDbO9IDSwBK6TbQa+PXYPCPy6rbTrTtw7PHkccKrpp0yVhp5HdEIcKr6pLlVDBfOLX9QUsyCOV0wzfjIJNlGEYsdlLJizHhbn2mUjvSAHQqZETYP81eFzLQNnPHt4EVVUh7VfDESU84KezmD5QlWpXLmvU31/yMf+Se8xhHTvKSCZIFImWwoG6mbUoWf9nzpIoaSjB+weqqUUmpaaasXVal72J+UX2B+2RPW3RcT0eOzQgqlJL3RKrTJvdsjE3JEAvGq3lGHSZXy28G3skua2SmVi/w4yCE6gbODqnTWlg7+wC604ydGXA8VJiS5ap43JXiUFFAaQ==" > ~/.ssh/known_hosts;

if ! ./gradlew dist; then
  echo "Build error";
  exit -1;
fi

if ! (mv ./desktop/build/libs/desktop-1.0.jar ../ && mv ./html/build/dist ../html && git checkout --orphan gh-pages && git reset --hard && git clean -d -f -x && git pull origin gh-pages && (rm -r "./auto/html/$TRAVIS_BRANCH" || true) && (rm -r "./auto/desktop/$TRAVIS_BRANCH" || true) && mkdir "./auto/desktop/$TRAVIS_BRANCH" && mv ../desktop-1.0.jar "./auto/desktop/$TRAVIS_BRANCH" && mv ../html "./auto/html/$TRAVIS_BRANCH" && BRANCHES=$(ls ./auto/desktop) && echo "<html><table>" > ./auto/index.html && (for i in $BRANCHES; do echo "<tr><td>$i</td><td><a href=\"desktop/$i/desktop-1.0.jar\">Desktop</a></td><td><a href=\"html/$i/\">html</a></td></tr>" >> ./auto/index.html; done;) && echo "</table></html>" >> ./auto/index.html && git add -A && git commit -a -m "Automatic commit"); then
  echo "Git error 1";
  exit -1;
fi



until (git push origin gh-pages); do
  echo "Push failed. Trying again in 10s.";
  sleep 10;
  git merge --abort;
  git pull origin gh-pages;
  if ! (BRANCHES=$(ls ./auto/desktop) && echo "<html><table>" > ./auto/index.html && (for i in $BRANCHES; do echo "<tr><td>$i</td><td><a href=\"desktop/$i/desktop-1.0.jar\">Desktop</a></td><td><a href=\"html/$i/\">html</a></td></tr>" >> ./auto/index.html; done;) && echo "</table></html>" >> ./auto/index.html && git add -A && git commit -a -m "Automatic commit"); then
    echo "Git error 2";
    exit -1;
  fi
done
