/**
 * Copyright © 2019 dataliquid GmbH | www.dataliquid.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dataliquid.maven.distribution.verifier.domain;

public class ResultEntry
{

    private String status;
    private String message;
    private String path;
    private String md5;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getMd5()
    {
        return md5;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResultEntry other = (ResultEntry) obj;
        if (md5 == null)
        {
            if (other.md5 != null)
                return false;
        }
        else if (!md5.equals(other.md5))
            return false;
        if (message == null)
        {
            if (other.message != null)
                return false;
        }
        else if (!message.equals(other.message))
            return false;
        if (path == null)
        {
            if (other.path != null)
                return false;
        }
        else if (!path.equals(other.path))
            return false;
        if (status == null)
        {
            if (other.status != null)
                return false;
        }
        else if (!status.equals(other.status))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ResultEntry [status=" + status + ", message=" + message + ", path=" + path + ", md5=" + md5 + "]";
    }

}
