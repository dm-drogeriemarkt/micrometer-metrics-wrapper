#!/usr/bin/env bash
KEY_FILE='dominik.johs.open.source.private.key'

echo $GPG_KEY_BASE64 | base64 -d  > ${KEY_FILE}
gpg --passphrase "${GPG_PASSPHRASE}" --batch --yes --fast-import ${KEY_FILE}

if [[ "${REF_TYPE}" == "tag" ]]; then
    mvn --settings .github/mvnsettings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=${REF_NAME} 1>/dev/null 2>/dev/null
    mvn deploy -P sign,build-extras --settings .github/mvnsettings.xml -DskipTests=true -B -U
    SUCCESS=$?
else
    echo "this should only be run for tags"
    SUCCESS=1
fi

# just to be safe, although this deleting these should not be necessary
rm ${KEY_FILE}
rm -rf ~/.gnupg

exit ${SUCCESS}
