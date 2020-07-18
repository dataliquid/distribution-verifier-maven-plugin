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

import java.util.List;

public class VerifierResult
{

    private final boolean valid;
    private final List<ResultEntry> resultEntries;
    
    public VerifierResult(boolean valid, List<ResultEntry> resultEntries)
    {
        super();
        this.valid = valid;
        this.resultEntries = resultEntries;
    }
    
    public boolean isValid()
    {
        return valid;
    }

    public List<ResultEntry> getResultEntries()
    {
        return resultEntries;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resultEntries == null) ? 0 : resultEntries.hashCode());
        result = prime * result + (valid ? 1231 : 1237);
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
        VerifierResult other = (VerifierResult) obj;
        if (resultEntries == null)
        {
            if (other.resultEntries != null)
                return false;
        }
        else if (!resultEntries.equals(other.resultEntries))
            return false;
        if (valid != other.valid)
            return false;
        return true;
    }
    @Override
    public String toString()
    {
        return "VerifierResult [valid=" + valid + ", resultEntries=" + resultEntries + "]";
    }

}
