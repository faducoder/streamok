/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.lib.process

import groovy.transform.ToString
import net.streamok.lib.conf.Conf

@ToString(includeNames = true, includeFields = true)
class Command {

    // Members

    private final List<String> command

    private final File workingDirectory

    private final Map<String, String> environment

    private final boolean sudo

    private final String sudoPassword

    // Constructors

    Command(List<String> command, File workingDirectory, Map<String, String> environment, boolean sudo, String sudoPassword) {
        this.command = command
        this.workingDirectory = workingDirectory
        this.environment = environment
        this.sudo = sudo
        this.sudoPassword = sudoPassword
    }

    static Command cmd(boolean sudo, String... command) {
        if(command.length == 1 && command[0] =~ /\s+/) {
            cmd(sudo, command[0].split(/\s+(?=([^"]*"[^"]*")*[^"]*$)/)) // don't split values by whitespaces inside " "
        } else {
            new Command(command.toList(), null, null, sudo, null)
        }
    }

    static Command cmd(String... command) {
        cmd(false, command)
    }

    static Command sudo(String... command) {
        cmd(true, command)
    }

    // Members access

    List<String> command() {
        return command
    }

    File workingDirectory() {
        workingDirectory
    }

    Map<String, String> environment() {
        environment
    }

    boolean sudo() {
        sudo
    }

    // Member resolvers

    String sudoPassword() {
        if(sudoPassword != null) {
            return sudoPassword
        }
        Conf.configuration().instance().getString('SUDO_PASSWORD')
    }

}
