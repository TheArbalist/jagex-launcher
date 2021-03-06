/*
 * Copyright 2001-2002 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

class StatSamplerTask;

/*
 * The StatSampler class is responsible for periodically updating
 * sampled PerfData instances and writing the sampled values to the
 * PerfData memory region.
 *
 * In addition it is also responsible for providing a home for
 * PerfData instances that otherwise have no better home.
 */
class StatSampler : AllStatic {

  friend class StatSamplerTask;

  private:

    static StatSamplerTask* _task;
    static PerfDataList* _sampled;

    static void collect_sample();
    static void create_misc_perfdata();
    static void create_sampled_perfdata();
    static void sample_data(PerfDataList* list);
    static const char* get_system_property(const char* name, TRAPS);
    static void create_system_property_instrumentation(TRAPS);

  public:
    // Start/stop the sampler
    static void engage();
    static void disengage();

    static bool is_active() { return _task != NULL; }

    static void initialize();
    static void destroy();
};

void statSampler_exit();
